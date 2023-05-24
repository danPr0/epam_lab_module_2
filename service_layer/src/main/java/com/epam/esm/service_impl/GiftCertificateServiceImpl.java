package com.epam.esm.service_impl;

import com.epam.esm.dto.GiftCertificateDTO;
import com.epam.esm.dto.TagDTO;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.util_service.DTOUtil;
import com.epam.esm.util_service.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of service interface {@link com.epam.esm.service.GiftCertificateService}.
 *
 * @author Danylo Proshyn
 */

@Service
public class GiftCertificateServiceImpl implements GiftCertificateService {

    private final GiftCertificateRepository    gcRepository;
    private final TagRepository                tagRepository;
    private final DataSourceTransactionManager transactionManager;

    @Autowired
    public GiftCertificateServiceImpl(
            GiftCertificateRepository gcRepository, TagRepository tagRepository,
            DataSourceTransactionManager transactionManager) {

        this.gcRepository       = gcRepository;
        this.tagRepository      = tagRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    public boolean addGiftCertificate(GiftCertificateDTO gcDTO) {

        GiftCertificate gc = DTOUtil.convertToEntity(gcDTO);
        gc.setCreateDate(LocalDateTime.now());
        gc.setLastUpdateDate(LocalDateTime.now());

        TransactionStatus ts = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            gcRepository.insertEntity(gc);

            addTagsToGiftCertificate(gcDTO);

            transactionManager.commit(ts);
        } catch (DataAccessException e) {
            transactionManager.rollback(ts);

            return false;
        }

        return true;
    }

    @Override
    public Optional<GiftCertificateDTO> getGiftCertificate(long id) {

        Optional<GiftCertificate> gc = gcRepository.getEntity(id);

        return gc.map(
                giftCertificate -> DTOUtil.convertToDTO(giftCertificate, tagRepository.getAllByGiftCertificate(id)));
    }

    @Override
    public boolean updateGiftCertificate(GiftCertificateDTO gcDTO) {

        TransactionStatus ts = transactionManager.getTransaction(
                new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));

        try {
            GiftCertificate gc = gcRepository.getEntity(gcDTO.getId()).orElseThrow(() -> new DataAccessException("") {
            });
            if (gcDTO.getName() != null) {
                gc.setName(gcDTO.getName());
            }
            if (gcDTO.getDescription() != null) {
                gc.setDescription(gcDTO.getDescription());
            }
            if (gcDTO.getPrice() != null) {
                gc.setPrice(gcDTO.getPrice());
            }
            if (gcDTO.getDuration() != null) {
                gc.setDuration(gcDTO.getDuration());
            }
            gc.setLastUpdateDate(LocalDateTime.now());

            gcRepository.updateEntity(gc);

            gcRepository.deleteAllTagsForEntity(gc.getId());

            addTagsToGiftCertificate(gcDTO);

            transactionManager.commit(ts);
        } catch (DataAccessException e) {
            transactionManager.rollback(ts);

            return false;
        }

        return true;
    }

    @Override
    public boolean deleteGiftCertificate(long id) {

        return gcRepository.deleteEntity(id) != 0;
    }

    @Override
    public List<GiftCertificateDTO> getAll(
            Optional<String> tagName, Optional<String> namePart, Optional<String> descriptionPart,
            Optional<Order> nameOrder, Optional<Order> createDateOrder) {

        return gcRepository.getAll(tagName, namePart, descriptionPart, nameOrder.map(Enum::name),
                        createDateOrder.map(Enum::name)).stream()
                .map(gc -> DTOUtil.convertToDTO(gc, tagRepository.getAllByGiftCertificate(gc.getId()))).toList();
    }

    private void addTagsToGiftCertificate(GiftCertificateDTO gcDTO) {

        if (gcDTO.getTags() != null) {
            for (TagDTO tag : gcDTO.getTags()) {

                if (tagRepository.getEntity(tag.getId()).isEmpty()) {
                    tagRepository.insertEntity(DTOUtil.convertToEntity(tag));
                }

                gcRepository.addTagToEntity(gcDTO.getId(), tag.getId());
            }
        }
    }
}
