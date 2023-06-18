package cn.wenhe9.question.domian.repository;

import cn.wenhe9.question.core.JpaRepositoryTest;
import cn.wenhe9.question.domian.model.Question;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * @description:
 * @author: DuJinliang
 * @create: 2023/6/18
 */
@JpaRepositoryTest
class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    /**
     * 测试仓储接口能够正确保存question并注入id
     */
    @Test
    public void repository_should_successfully_save_question() {
        var question = new Question("UID_0001", "A test title", "A test detail");

        var savedQuestion = questionRepository.save(question);

        assertThat(savedQuestion.getId(), is(notNullValue()));
        assertThat(savedQuestion.getQuestionerId(), equalTo(question.getQuestionerId()));
        assertThat(savedQuestion.getTitle(), equalTo(question.getTitle()));
        assertThat(savedQuestion.getDetail(), equalTo(question.getDetail()));
    }
}
