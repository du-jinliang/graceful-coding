package cn.wenhe9.question.interfaces.question.face.request;

/**
 * @description:
 * @author: DuJinliang
 * @create: 2023/6/18
 */
public record CreateQuestionRequest(
        String questionerId,
        String title,
        String detail
) {
}
