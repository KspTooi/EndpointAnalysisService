package com.ksptool.bio.biz.gen.service;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import com.ksptool.bio.biz.gen.model.outblueprint.GenOutBlueprintPo;
import com.ksptool.bio.biz.gen.model.outblueprint.dto.AddOutBlueprintDto;
import com.ksptool.bio.biz.gen.model.outblueprint.dto.EditOutBlueprintDto;
import com.ksptool.bio.biz.gen.model.outblueprint.dto.GetOutBlueprintListDto;
import com.ksptool.bio.biz.gen.model.outblueprint.vo.GetOutBlueprintDetailsVo;
import com.ksptool.bio.biz.gen.model.outblueprint.vo.GetOutBlueprintListVo;
import com.ksptool.bio.biz.gen.repository.OutBlueprintRepository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.transport.ssh.jsch.JschConfigSessionFactory;
import org.eclipse.jgit.transport.ssh.jsch.OpenSshConfig;
import org.eclipse.jgit.util.FS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;


@Service
public class OutBlueprintService {

    @Autowired
    private OutBlueprintRepository repository;

    /**
     * 获取输出蓝图列表
     *
     * @param dto 查询条件
     * @return 输出蓝图列表
     */
    public PageResult<GetOutBlueprintListVo> getOutBlueprintList(GetOutBlueprintListDto dto) {
        GenOutBlueprintPo query = new GenOutBlueprintPo();
        assign(dto, query);

        Page<GenOutBlueprintPo> page = repository.getOutBlueprintList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetOutBlueprintListVo> vos = as(page.getContent(), GetOutBlueprintListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增输出蓝图
     *
     * @param dto 新增数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void addOutBlueprint(AddOutBlueprintDto dto) {
        GenOutBlueprintPo insertPo = as(dto, GenOutBlueprintPo.class);
        repository.save(insertPo);
    }

    /**
     * 编辑输出蓝图
     *
     * @param dto 编辑数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void editOutBlueprint(EditOutBlueprintDto dto) throws BizException {
        GenOutBlueprintPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    /**
     * 获取输出蓝图详情
     *
     * @param dto 查询条件
     * @return
     */
    public GetOutBlueprintDetailsVo getOutBlueprintDetails(CommonIdDto dto) throws BizException {
        GenOutBlueprintPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetOutBlueprintDetailsVo.class);
    }

    /**
     * 删除输出蓝图
     *
     * @param dto 删除条件
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeOutBlueprint(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

    /**
     * 测试SCM连接
     *
     * @param dto 测试条件
     */
    public Result<String> testScmConnection(CommonIdDto dto) throws BizException {
        GenOutBlueprintPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("测试连接失败,数据不存在或无权限访问."));

        try {

            var startTime = System.currentTimeMillis();

            LsRemoteCommand lrc = Git.lsRemoteRepository()
                    .setRemote(po.getScmUrl())
                    .setTimeout(10); // 设置超时

            
            // 根据不同的认证方式进行配置
            if (po.getScmAuthKind() == 1) { //账号密码/Token
                lrc.setCredentialsProvider(
                        new UsernamePasswordCredentialsProvider(po.getScmUsername(), po.getScmPassword())
                );
            }

            //SSH KEY
            if (po.getScmAuthKind() == 2) {
                setupSshAuthentication(lrc, po.getScmPk());
            }

            //PAT
            if (po.getScmAuthKind() == 3) {
                lrc.setCredentialsProvider(
                        new UsernamePasswordCredentialsProvider(po.getScmUsername(), po.getScmPassword())
                );
            }

            //执行请求获取远程分支
            Collection<Ref> refs = lrc.call();
            
            //校验远程分支是否存在
            if (refs.isEmpty()) {
                throw new BizException("连接成功,但远程分支为空");
            }

            // 兼容处理：用户可能输入 "main" 或 "refs/heads/main"
            String targetBranch = po.getScmBranch();
            String fullBranchPath = targetBranch.startsWith("refs/") ? targetBranch : "refs/heads/" + targetBranch;
            
            boolean branchExists = refs.stream()
                .anyMatch(ref -> ref.getName().equalsIgnoreCase(fullBranchPath));

            if (!branchExists) {
                throw new BizException("连接成功,但分支[" + po.getScmBranch() + "]不存在");
            }

            //测试成功
            return Result.success("测试成功 耗时: " + (System.currentTimeMillis() - startTime) + "ms",null);

        } catch (GitAPIException e) {
            throw new BizException("Git 连接失败: " + e.getMessage());
        } catch (Exception e) {
            throw new BizException("系统异常: " + e.getMessage());
        }

    }

    private void setupSshAuthentication(LsRemoteCommand command, String privateKey) {
        SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                session.setConfig("StrictHostKeyChecking", "no"); // 禁用严格主机密钥检查
            }

            @Override
            protected JSch createDefaultJSch(FS fs) throws JSchException {
                JSch jsch = super.createDefaultJSch(fs);
                jsch.removeAllIdentity();
                // 将数据库中的私钥字符串添加为身份信息
                jsch.addIdentity("git-ssh-key", privateKey.getBytes(), null, null);
                return jsch;
            }
        };

        command.setTransportConfigCallback(transport -> {
            if (transport instanceof SshTransport) {
                ((SshTransport) transport).setSshSessionFactory(sshSessionFactory);
            }
        });
    }

}