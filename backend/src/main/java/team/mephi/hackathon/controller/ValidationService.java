package team.mephi.hackathon.controller;

import team.mephi.hackathon.dto.TransactionRequestDto;

public interface ValidationService {
  void validateTransaction(TransactionRequestDto dto);
}
