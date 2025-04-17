package team.mephi.hackathon.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//test -  commit
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TransactionController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @GetMapping("/transactions")
    public ResponseEntity<String> getTransactions() {
        logger.info("Test log record");
        return ResponseEntity.ok().body("ok");
    }
}
