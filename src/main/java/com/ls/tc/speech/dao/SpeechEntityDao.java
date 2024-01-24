package com.ls.tc.speech.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ls.tc.speech.dao.entity.SpeechEntity;

public interface SpeechEntityDao extends JpaRepository<SpeechEntity, String> {

}
