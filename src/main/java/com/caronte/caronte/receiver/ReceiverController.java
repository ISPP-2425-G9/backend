package com.caronte.caronte.receiver;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.obituary.Obituary;
import com.caronte.caronte.obituary.ObituaryService;
import com.caronte.caronte.receiver.DTOs.ReceiverResponseDTO;

@RestController
@RequestMapping("api/receiver")
public class ReceiverController {
    
    private final ReceiverService receiverService;
    private final ObituaryService obituaryService;

    public ReceiverController(ReceiverService receiverService, ObituaryService obituaryService) {
        this.receiverService = receiverService;
        this.obituaryService = obituaryService;
    }

    @GetMapping("/getReceivers/obituary/{obituaryId}")
    public ResponseEntity<List<ReceiverResponseDTO>> getReceiversByObituaryId(@PathVariable Long obituaryId, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        Long userId = userDetailsImpl.getId();
        Obituary obituary = obituaryService.getObituaryById(obituaryId, userId);
        List<ReceiverResponseDTO> receivers = receiverService.getReceiversByObituaryId(obituary);
        return ResponseEntity.ok().body(receivers);
    }

}
