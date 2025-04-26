package team.mephi.hackathon.service;

import org.springframework.stereotype.Service;
import team.mephi.hackathon.controller.ValidationService;
import team.mephi.hackathon.dto.TransactionRequestDto;
import team.mephi.hackathon.exceptions.ValidationException;

@Service
public class ValidationServiceImpl implements ValidationService {

    public void validateTransaction(TransactionRequestDto dto) {
        validateInn(dto.getReceiverInn());
        validatePhone(dto.getReceiverPhone());
    }

    private void validateInn(String inn) {
        if (inn == null) return;

        int length = inn.length();
        if (length != 10 && length != 12) {
            throw new ValidationException("ИНН должен содержать 10 или 12 цифр");
        }

        if (!inn.matches("\\d+")) {
            throw new ValidationException("ИНН должен содержать только цифры");
        }
    }

    private void validatePhone(String phone) {
        if (phone == null) return;

        if (!phone.matches("^[78]\\d{10}$")) {
            throw new ValidationException("Телефон должен быть в формате 7XXXXXXXXXX или 8XXXXXXXXXX");
        }
    }
}