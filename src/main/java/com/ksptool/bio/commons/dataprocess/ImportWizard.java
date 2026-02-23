package com.ksptool.bio.commons.dataprocess;

import com.alibaba.excel.EasyExcel;
import com.ksptool.assembly.entity.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public class ImportWizard<T extends AbstractImportDto> {

    private static final Logger log = LoggerFactory.getLogger(ImportWizard.class);

    //文件
    private MultipartFile file;

    //目标DTO类
    private Class<T> targetClazz;

    //容器列表
    private List<T> data;

    private int rowStart = 2;

    public ImportWizard(MultipartFile file, Class<T> targetClazz) throws BizException {
        if (file == null) {
            throw new BizException("文件不能为空");
        }
        if (file.isEmpty()) {
            throw new BizException("文件不能为空");
        }
        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            throw new BizException("文件格式错误,应为xlsx格式");
        }
        this.file = file;
        this.targetClazz = targetClazz;
    }

    /**
     * 用于将excel文件传输为目标DTO列表
     *
     * @return 转换后的DTO列表
     * @throws BizException 如果转换失败则抛出异常
     */
    public void transfer() throws BizException {

        try (var is = file.getInputStream()) {

            List<T> list = EasyExcel.read(is).head(targetClazz).sheet().doReadSync();

            if (list == null || list.isEmpty()) {
                throw new BizException("导入数据为空");
            }

            data = list;

        } catch (Exception e) {
            log.error("文件读取失败", e);
            throw new BizException("文件读取失败: " + e.getMessage());
        }

    }

    /**
     * 用于验证导入数据
     *
     * @return 验证结果 如果验证通过则返回null 否则返回错误信息
     */
    public String validate() {

        if (data == null || data.isEmpty()) {
            return "导入数据为空";
        }

        var row = rowStart;

        for (var item : data) {
            var error = item.validate();
            if (error != null) {
                return "第" + row + "行数据有误: " + error;
            }
            row++;
        }
        return null;
    }

    public List<T> getData() throws BizException {

        if (data == null || data.isEmpty()) {
            throw new BizException("导入数据为空");
        }

        return data;
    }

    public void setRowStart(int rowStart) {
        this.rowStart = rowStart;
    }


}
