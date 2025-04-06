package com.caronte.caronte.receiver;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.caronte.caronte.obituary.Obituary;
import com.caronte.caronte.util.LongRepository;

@Repository
public interface ReceiverRepository extends LongRepository<Receiver> {

    void deleteByObituary(Obituary obituary);

    List<Receiver> findByObituary(Obituary obituary);

    @Query("SELECT r FROM Receiver r WHERE r.message.id = :messageId")
    List<Receiver> findByMessageId(Long messageId);

    @Query("SELECT r FROM Receiver r WHERE (r.telephone = :telephone AND r.email = :email) AND r.message.id = :messageId")
    Optional<Receiver> findByMessageIdAndTelephoneOrEmail(@Param("messageId") Long messageId, 
                                                          @Param("telephone") String telephone, 
                                                          @Param("email") String email);
} 
