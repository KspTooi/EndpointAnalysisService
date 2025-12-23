package com.ksptooi.biz.core.service;


import com.ksptooi.biz.core.model.attach.AttachPo;
import com.ksptooi.biz.core.model.attach.dto.PreCheckAttachDto;
import com.ksptooi.biz.core.model.attach.vo.ApplyChunkVo;
import com.ksptooi.biz.core.model.attach.vo.PreCheckAttachVo;
import com.ksptooi.biz.core.repository.AttachChunkRepository;
import com.ksptooi.biz.core.repository.AttachRepository;
import com.ksptooi.commons.config.AttachConfig;
import com.ksptooi.commons.utils.IdWorker;
import com.ksptool.assembly.entity.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 文件附件服务(通用)
 */
@Service
@Slf4j
public class AttachService {

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy_MM_dd");

    @Autowired
    private AttachRepository repository;

    @Autowired
    private AttachChunkRepository chunkRepository;

    @Autowired
    private AttachConfig attachConfig;

    @Autowired
    @Lazy
    private AttachService self;

    /**
     * 预检文件
     *
     * @param dto 预检文件参数
     * @return 预检文件结果
     * @throws BizException 预检文件失败
     */
    @Transactional(rollbackFor = Exception.class)
    public PreCheckAttachVo preCheckAttach(PreCheckAttachDto dto) throws BizException {

        var ret = new PreCheckAttachVo();
        //ret.setPreCheckId(IdWorker.nextId());

        //校验文件是否存在
        var attach = repository.getBySha256AndKind(dto.getSha256(), dto.getKind());

        if (attach == null) {
            var relativePath = getAttachRelativePath(dto.getSha256(), getSuffix(dto.getName()));

            if (relativePath == null) {
                throw new BizException("获取附件本地路径失败");
            }

            var absolutePath = getAttachLocalPath(relativePath);

            //创建预检记录
            AttachPo insertPo = new AttachPo();
            insertPo.setId(IdWorker.nextId());
            insertPo.setName(dto.getName());
            insertPo.setKind(dto.getKind());
            insertPo.setSuffix(getSuffix(dto.getName()));
            insertPo.setPath(relativePath.toString());
            insertPo.setSha256(dto.getSha256());
            insertPo.setTotalSize(dto.getTotalSize());
            insertPo.setReceiveSize(0L);
            insertPo.setStatus(0);
            insertPo.setChunks(new ArrayList<>());
            repository.save(insertPo);

            ret.setPreCheckId(insertPo.getId());
            ret.setName(insertPo.getName());
            ret.setKind(insertPo.getKind());
            ret.setPath(insertPo.getPath());
            ret.setStatus(0);
            long chunkCount = getChunkCount(dto.getTotalSize(), getChunkSize());
            List<Long> missingChunkIds = new ArrayList<>();
            for (long i = 0; i < chunkCount; i++) {
                missingChunkIds.add(i);
            }
            ret.setMissingChunkIds(missingChunkIds);

            //预分配文件
            preAllocateAttach(absolutePath, dto.getTotalSize());
            return ret;
        }

        ret.setPreCheckId(attach.getId());

        //0:预检文件 1:区块不完整 2:有效
        if (attach.getStatus() == 0 || attach.getStatus() == 1) {
            ret.setPreCheckId(attach.getId());
            ret.setName(attach.getName());
            ret.setKind(attach.getKind());
            ret.setPath(Paths.get(attach.getPath()).toString());
            ret.setStatus(1);

            //计算缺失分块
            long chunkCount = getChunkCount(attach.getTotalSize(), getChunkSize());
            Set<Long> existChunkIds = new HashSet<>();
            for (var chunk : attach.getChunks()) {
                existChunkIds.add(chunk.getChunkId());
            }
            List<Long> missingChunkIds = new ArrayList<>();

            for (long i = 0; i < chunkCount; i++) {
                if (!existChunkIds.contains(i)) {
                    missingChunkIds.add(i);
                }
            }
            ret.setMissingChunkIds(missingChunkIds);
            return ret;
        }

        //2:有效
        if (attach.getStatus() == 2) {
            ret.setName(attach.getName());
            ret.setKind(attach.getKind());
            ret.setPath(Paths.get(attach.getPath()).toString());
            ret.setStatus(2);
            ret.setMissingChunkIds(Collections.emptyList());
            return ret;
        }

        //3:校验中
        if (attach.getStatus() == 3) {
            ret.setName(attach.getName());
            ret.setKind(attach.getKind());
            ret.setPath(Paths.get(attach.getPath()).toString());
            ret.setStatus(3);
            ret.setMissingChunkIds(Collections.emptyList());
            return ret;
        }

        return ret;
    }


    @Transactional(rollbackFor = Exception.class)
    public ApplyChunkVo applyChunk(Long preCheckId, Long chunkId, MultipartFile file) throws BizException {

        var attach = repository.findById(preCheckId).orElseThrow(() -> new BizException("预检文件不存在"));
        var chunkSize = file.getSize();
        var chunkCount = getChunkCount(attach.getTotalSize(), getChunkSize());

        if (attach.getStatus() == 2 || attach.getStatus() == 3) {
            throw new BizException("附件已完整或正在进行校验,无法应用区块!");
        }

        if (chunkSize > attach.getTotalSize()) {
            throw new BizException("区块大小超过文件总大小!");
        }

        if (chunkId >= chunkCount || chunkId < 0) {
            throw new BizException("区块ID范围错误! 最大为:" + chunkCount);
        }

        if ((attach.getReceiveSize() + chunkSize) > attach.getTotalSize()) {
            throw new BizException("应用该区块后,文件大小将超过总大小!");
        }

        //查询是否已重复应用
        var count = chunkRepository.countByAttachIdAndChunkId(attach.getId(), chunkId);

        if (count > 0) {
            throw new BizException("该区块已被应用.");
        }

        var absolutePath = getAttachLocalPath(Paths.get(attach.getPath()));

        if (!Files.exists(absolutePath)) {
            throw new BizException("文件系统错误,文件不存在,请确认该附件已预检过: " + absolutePath.toString());
        }

        //计算写入位置
        long offset = chunkId * getChunkSize();

        //写入区块到文件中
        try (RandomAccessFile raf = new RandomAccessFile(absolutePath.toString(), "rw");
             InputStream inputStream = file.getInputStream()) {

            raf.seek(offset);
            byte[] buffer = new byte[8192];
            long remaining = chunkSize;

            while (remaining > 0) {
                int bytesRead = inputStream.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                if (bytesRead == -1) {
                    break;
                }
                raf.write(buffer, 0, bytesRead);
                remaining -= bytesRead;
            }
        } catch (IOException e) {
            log.error("写入区块失败 附件ID:{} 区块ID:{} 错误:{}", attach.getId(), chunkId, e.getMessage(), e);
            throw new BizException("写入区块失败: " + e.getMessage());
        }


        var ret = new ApplyChunkVo();
        ret.setAttachId(attach.getId());
        ret.setChunkTotal(chunkCount.intValue());

        //保存分块记录
        attach.addChunk(chunkId);

        //更新已接收大小
        attach.setReceiveSize(attach.getReceiveSize() + chunkSize);

        //检查是否所有分块都已上传完成
        var chunkPos = attach.getChunks();

        ret.setChunkApplied(chunkPos.size());

        if (chunkPos.size() != chunkCount) {
            return ret;
        }

        //所有分块都已上传完成 更新为校验中状态
        if (chunkPos.size() == chunkCount) {
            attach.setStatus(2);
            //异步校验
            self.verifyAttach(attach);
        }

        repository.save(attach);
        return ret;
    }


    @Async
    public void verifyAttach(AttachPo attach) {

        if (attach == null) {
            return;
        }

        var absolutePath = getAttachLocalPath(Paths.get(attach.getPath()));

        if (!Files.exists(absolutePath)) {
            log.error("校验文件不存在 ID:{} 路径:{}", attach.getId(), absolutePath);
            return;
        }

        try (InputStream is = Files.newInputStream(absolutePath)) {

            var digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }

            byte[] hashBytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            String actualSha256 = sb.toString();

            // 校验失败
            if (!StringUtils.equalsIgnoreCase(actualSha256, attach.getSha256())) {
                log.error("文件校验失败 ID:{} 预期:{} 实际:{}", attach.getId(), attach.getSha256(), actualSha256);
                attach.setStatus(1); // 回退到 1:区块不完整
                repository.save(attach);
                return;
            }

            // 校验成功
            attach.setStatus(3); // 3:有效
            attach.setVerifyTime(LocalDateTime.now());
            repository.save(attach);
            log.info("文件校验成功 ID:{} SHA256:{}", attach.getId(), actualSha256);

        } catch (Exception e) {
            log.error("文件校验异常 ID:{} 错误:{}", attach.getId(), e.getMessage(), e);
            attach.setStatus(1); // 发生异常也回退状态
            repository.save(attach);
        }

    }


    /**
     * 根据文件大小和分块大小计算分块数量
     *
     * @param bytes     文件大小
     * @param chunkSize 分块大小
     * @return 分块数量
     */
    public static Long getChunkCount(Long bytes, Long chunkSize) {
        return (bytes + chunkSize - 1) / chunkSize;
    }

    /**
     * 获取分块大小
     *
     * @return 分块大小 5MB
     */
    public static long getChunkSize() {
        return 5 * 1024 * 1024;
    }


    /**
     * 获取文件本地路径
     *
     * @return 文件本地路径 如果获取失败返回null
     * 按日期创建文件夹 /root/2025_12_18/SHA256
     */
    public Path getAttachLocalPath(Path relativePath) {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return Paths.get(attachConfig.getLocalWindowsPath()).resolve(relativePath);
        }
        if (osName.contains("linux")) {
            return Paths.get(attachConfig.getLocalLinuxPath()).resolve(relativePath);
        }
        throw new RuntimeException("不支持的操作系统: " + osName);
    }

    /**
     * 获取文件相对路径
     *
     * @return 文件相对路径 如果获取失败返回null
     * 按日期创建文件夹 /2025_12_18/SHA256
     */
    public Path getAttachRelativePath(String sha256, String suffix) {

        String osName = System.getProperty("os.name").toLowerCase();

        String storePath = null;

        if (osName.contains("win")) {
            storePath = attachConfig.getLocalWindowsPath();
        }

        if (osName.contains("linux")) {
            storePath = attachConfig.getLocalLinuxPath();
        }

        if (!osName.contains("win") && !osName.contains("linux")) {
            return null;
        }

        if (storePath == null) {
            return null;
        }

        var _suffix = StringUtils.isBlank(suffix) ? "" : "." + suffix;
        var _date = dateFormat.format(LocalDateTime.now());

        var rootPath = Paths.get(storePath);
        var absolutePathDir = rootPath.resolve(_date);
        var relativePathFile = Paths.get(_date).resolve(sha256 + _suffix);

        if (!Files.exists(absolutePathDir)) {
            try {
                Files.createDirectories(absolutePathDir);
            } catch (IOException e) {
                log.error("自动创建资源文件夹失败 路径:{} 错误:{}", storePath, e.getMessage(), e);
                return null;
            }
        }

        return relativePathFile;
    }


    /**
     * 获取文件后缀
     *
     * @param name 文件名
     * @return 文件后缀
     */
    public static String getSuffix(String name) {
        if (StringUtils.isBlank(name)) {
            return "";
        }

        int lastDotIndex = name.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == name.length() - 1) {
            return "";
        }

        return name.substring(lastDotIndex + 1);
    }

    /**
     * 预分配文件 写入一个指定大小的占位文件
     *
     * @throws BizException 预分配文件失败
     */
    public void preAllocateAttach(Path path, Long totalSize) throws BizException {

        if (path == null || path.toString().isBlank()) {
            throw new BizException("附件路径不能为空");
        }

        if (totalSize == null || totalSize <= 0) {
            throw new BizException("附件大小无效");
        }

        if (Files.exists(path)) {
            throw new BizException("无法预分配文件,文件已存在: " + path.toString());
        }

        try {
            var parentDir = path.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            try (RandomAccessFile raf = new RandomAccessFile(path.toString(), "rw")) {
                raf.setLength(totalSize);
            }
        } catch (IOException e) {
            log.error("预分配文件失败 路径:{} 大小:{} 错误:{}", path.toString(), totalSize, e.getMessage(), e);
            throw new BizException("预分配文件失败: " + e.getMessage());
        }
    }

}