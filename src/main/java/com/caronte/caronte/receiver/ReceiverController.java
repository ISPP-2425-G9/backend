package com.caronte.caronte.receiver;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caronte.caronte.obituary.Obituary;
import com.caronte.caronte.obituary.ObituaryService;
import com.caronte.caronte.user.UserService;

@RestController
@RequestMapping("api/receiver")
public class ReceiverController {

    private final ReceiverService receiverService;
    private final ObituaryService obituaryService;
    private final UserService userService;

    public ReceiverController(ReceiverService receiverService, ObituaryService obituaryService, UserService userService) {
        this.receiverService = receiverService;
        this.obituaryService = obituaryService;
        this.userService = userService;
    }

    @GetMapping("/getReceivers/obituary/{obituaryId}")
    public ResponseEntity<?> getReceiversByObituaryId(@PathVariable Long obituaryId) {
        Long userId = userService.findCurrentUser().getId();
        Obituary obituary = obituaryService.getObituaryById(obituaryId, userId);
        List<ReceiverResponseDTO> receivers = receiverService.getReceiversByObituaryId(obituary);
        return ResponseEntity.ok().body(receivers);
    }

}
