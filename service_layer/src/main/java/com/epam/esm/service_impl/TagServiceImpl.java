package com.epam.esm.service_impl;

import com.epam.esm.dto.TagDTO;
import com.epam.esm.entity.Tag;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.service.TagService;
import com.epam.esm.util_service.DTOUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of service interface {@link com.epam.esm.service.TagService}.
 *
 * @author Danylo Proshyn
 */

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    private final Logger logger = LogManager.getLogger(TagServiceImpl.class);

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {

        this.tagRepository = tagRepository;
    }

    @Override
    public boolean addTag(TagDTO tag) {

        try {
            tagRepository.insertEntity(DTOUtil.convertToEntity(tag));
        } catch (DataAccessException e) {
            logger.error(e);

            return false;
        }

        return true;
    }

    @Override
    public Optional<TagDTO> getTag(long id) {

        Optional<Tag> tag = tagRepository.getEntity(id);

        return tag.map(DTOUtil::convertToDTO);
    }

    @Override
    public boolean deleteTag(long id) {

        return tagRepository.deleteEntity(id) != 0;
    }
}
