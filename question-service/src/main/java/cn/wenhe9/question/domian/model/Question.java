package cn.wenhe9.question.domian.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @description: 返回建立问题的结果
 * @author: DuJinliang
 * @create: 2023-6-18 16:37
 */
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String questionerId;

    private String title;

    private String detail;

    protected Question() {
    }

    public Question(String questionerId, String title, String detail) {
        this.questionerId = questionerId;
        this.title = title;
        this.detail = detail;
    }

    public String getId() {
        return id;
    }

    public String getQuestionerId() {
        return questionerId;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }
}
