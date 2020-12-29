package com.dk.cc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author dk
 * @since 2020-11-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("m_vidoe")
public class Vidoe implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "v_id", type = IdType.AUTO)
    private Long vId;

    /**
     * 文件名
     */
    private String vName;

    /**
     * 存储地址
     */
    private String vPath;

    /**
     * 文件大小
     */
    private Long vSize;

    /**
     * 播放地址
     */
    private String vUrl;

    /**
     * 创建时间
     */
    private LocalDateTime created;


}
