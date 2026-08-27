package com.example.aiinterview.repository;

import com.example.aiinterview.entity.QuickReviewEvidence;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 学习证据仓储：汇聚三类“薄弱点证据”，供用户画像与快速复盘使用。
 * - findManualWeakEvidence：手动题库自测中得分偏低的题目
 * - findInterviewWeakEvidence：模拟面试中低分作答
 * - findQuickReviewWeakEvidence：快速复盘中答错的填空题
 * 这些证据是全项目唯一的画像输入来源，画像生成前会被聚合为纯文本传给大模型，
 * 因此本接口只负责“读”，不负责“写”（写由各业务模块自己完成）。
 */
@Mapper
public interface QuickReviewEvidenceRepository {

    List<QuickReviewEvidence> findManualWeakEvidence();

    List<QuickReviewEvidence> findInterviewWeakEvidence();

    List<QuickReviewEvidence> findQuickReviewWeakEvidence();
}
