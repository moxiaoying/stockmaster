package com.stock.master.pojo;

import java.util.Date;
import javax.persistence.*;

@Table(name = "t_user")
public class TUser {
    /**
     * 编号
     */
    @Id
    private Integer id;

    /**
     * 用户名称
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 用户密码
     */
    @Column(name = "pass_word")
    private String passWord;

    /**
     * 年龄
     */
    private Integer sex;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 获取编号
     *
     * @return id - 编号
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置编号
     *
     * @param id 编号
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取用户名称
     *
     * @return user_name - 用户名称
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置用户名称
     *
     * @param userName 用户名称
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取用户密码
     *
     * @return pass_word - 用户密码
     */
    public String getPassWord() {
        return passWord;
    }

    /**
     * 设置用户密码
     *
     * @param passWord 用户密码
     */
    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    /**
     * 获取年龄
     *
     * @return sex - 年龄
     */
    public Integer getSex() {
        return sex;
    }

    /**
     * 设置年龄
     *
     * @param sex 年龄
     */
    public void setSex(Integer sex) {
        this.sex = sex;
    }

    /**
     * 获取生日
     *
     * @return birthday - 生日
     */
    public Date getBirthday() {
        return birthday;
    }

    /**
     * 设置生日
     *
     * @param birthday 生日
     */
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}