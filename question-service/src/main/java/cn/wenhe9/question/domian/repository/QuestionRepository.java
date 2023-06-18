package cn.wenhe9.question.domian.repository;

import cn.wenhe9.question.domian.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @description: 查询仓储
 * @author: DuJinliang
 * @create: 2023/6/18
 */
public interface QuestionRepository extends JpaRepository<Question, String> {

}
