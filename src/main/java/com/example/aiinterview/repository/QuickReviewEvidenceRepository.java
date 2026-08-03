package com.example.aiinterview.repository;

import com.example.aiinterview.entity.QuickReviewEvidence;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuickReviewEvidenceRepository {

    List<QuickReviewEvidence> findManualWeakEvidence();

    List<QuickReviewEvidence> findInterviewWeakEvidence();

    List<QuickReviewEvidence> findQuickReviewWeakEvidence();
}
