package com.asiainfo.mcp.tmc.sso.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * <p>
 * 
 * </p>
 *
 * @author LG
 * @since 2022-09-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tmc_traing_course")
public class TmcTraingCourse implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("PROJECT_ID")
    private String projectId;

      @TableId("COURSE_ID")
    private String courseId;

    @TableField("COURSE_NAME")
    private String courseName;

    @TableField("TRAINING_SPEC")
    private String trainingSpec;

    @TableField("TRAINING_START_TIME")
    private LocalDate trainingStartTime;

    @TableField("TRAINING_END_TIME")
    private LocalDate trainingEndTime;

    @TableField("TRAING_DURATION")
    private BigDecimal traingDuration;

    @TableField("LECTURER_SOURCE")
    private String lecturerSource;

    @TableField("LECTURER_NAME")
    private String lecturerName;

    @TableField("LECTURER_ID")
    private String lecturerId;

    @TableField("TEACHING_METHOED")
    private String teachingMethoed;

    @TableField("LECTURER_FEE")
    private Integer lecturerFee;

    @TableField("SUPPLY_CRITERIA")
    private String supplyCriteria;

    @TableField("SUPPY_ID")
    private String suppyId;


}
