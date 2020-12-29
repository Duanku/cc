package com.dk.cc.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author dk
 * @since 2020-12-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("file_inf")
public class FileInf implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件编号
     */
    @TableId(value = "file_id", type = IdType.AUTO)
    private Long fileId;

    /**
     * 类型编号
     */
    private String cateId;

    /**
     * 文件夹编号
     */
    private String dirId;

    /**
     * 用户编号
     */
    private String userId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件存储位
     */
    private String filePath;

    /**
     * 文件大小(单位kb)
     */
    private Integer fileSize;

    /**
     * 上传时间
     */
    private String fileUploadTime;

    /**
     * 文件状态
     */
    private Integer fileStatus;


}
