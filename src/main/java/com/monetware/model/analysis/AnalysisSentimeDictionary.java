package com.monetware.model.analysis;

import java.util.Date;

public class AnalysisSentimeDictionary {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column analysis_sentime_dictionary.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column analysis_sentime_dictionary.name
     *
     * @mbg.generated
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column analysis_sentime_dictionary.words_no
     *
     * @mbg.generated
     */
    private Long wordsNo;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column analysis_sentime_dictionary.level
     *
     * @mbg.generated
     */
    private Byte level;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column analysis_sentime_dictionary.create_user
     *
     * @mbg.generated
     */
    private Integer createUser;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column analysis_sentime_dictionary.create_time
     *
     * @mbg.generated
     */
    private Date createTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column analysis_sentime_dictionary.id
     *
     * @return the value of analysis_sentime_dictionary.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column analysis_sentime_dictionary.id
     *
     * @param id the value for analysis_sentime_dictionary.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column analysis_sentime_dictionary.name
     *
     * @return the value of analysis_sentime_dictionary.name
     *
     * @mbg.generated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column analysis_sentime_dictionary.name
     *
     * @param name the value for analysis_sentime_dictionary.name
     *
     * @mbg.generated
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column analysis_sentime_dictionary.words_no
     *
     * @return the value of analysis_sentime_dictionary.words_no
     *
     * @mbg.generated
     */
    public Long getWordsNo() {
        return wordsNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column analysis_sentime_dictionary.words_no
     *
     * @param wordsNo the value for analysis_sentime_dictionary.words_no
     *
     * @mbg.generated
     */
    public void setWordsNo(Long wordsNo) {
        this.wordsNo = wordsNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column analysis_sentime_dictionary.level
     *
     * @return the value of analysis_sentime_dictionary.level
     *
     * @mbg.generated
     */
    public Byte getLevel() {
        return level;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column analysis_sentime_dictionary.level
     *
     * @param level the value for analysis_sentime_dictionary.level
     *
     * @mbg.generated
     */
    public void setLevel(Byte level) {
        this.level = level;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column analysis_sentime_dictionary.create_user
     *
     * @return the value of analysis_sentime_dictionary.create_user
     *
     * @mbg.generated
     */
    public Integer getCreateUser() {
        return createUser;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column analysis_sentime_dictionary.create_user
     *
     * @param createUser the value for analysis_sentime_dictionary.create_user
     *
     * @mbg.generated
     */
    public void setCreateUser(Integer createUser) {
        this.createUser = createUser;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column analysis_sentime_dictionary.create_time
     *
     * @return the value of analysis_sentime_dictionary.create_time
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column analysis_sentime_dictionary.create_time
     *
     * @param createTime the value for analysis_sentime_dictionary.create_time
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}