package com.epam.esm.service;

import com.epam.esm.dto.GiftCertificateDTO;
import com.epam.esm.util_service.Order;

import java.util.List;
import java.util.Optional;

/**
 * Service class for {@link com.epam.esm.dto.GiftCertificateDTO}.
 *
 * @author Danylo Proshyn
 */

public interface GiftCertificateService {

    boolean addGiftCertificate(GiftCertificateDTO gc);

    Optional<GiftCertificateDTO> getGiftCertificate(long id);

    boolean updateGiftCertificate(GiftCertificateDTO gc);

    boolean deleteGiftCertificate(long id);

    List<GiftCertificateDTO> getAll(
            Optional<String> tagName, Optional<String> namePart, Optional<String> descriptionPart,
            Optional<Order> nameOrder, Optional<Order> createDateOrder);
}
