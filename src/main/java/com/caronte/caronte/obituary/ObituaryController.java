package com.caronte.caronte.obituary;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caronte.caronte.receiver.ReceiverService;

@RestController
@RequestMapping("api/obituary")
public class ObituaryController {
        
    ReceiverService receiverService;
    ObituaryService obituaryService;

    


}
