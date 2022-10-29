package net.ausiasmarch.andamio.service;

import net.ausiasmarch.andamio.entity.ProjectEntity;
import net.ausiasmarch.andamio.exception.ResourceNotFoundException;
import net.ausiasmarch.andamio.repository.ProjectRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    private final ProjectRepository oProjectRepository;

    @Autowired
    AuthService oAuthService;

    @Autowired
    public ProjectService(ProjectRepository oProjectRepository) {
        this.oProjectRepository = oProjectRepository;
    }

    public void validate(Long id) {
        if (!oProjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("id " + id + " not exist");
        }
    }

    public ProjectEntity get(Long id) {
        oAuthService.OnlyAdmins();
        return oProjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project with id: " + id + " not found"));

    }

    public Page<ProjectEntity> getPage(Pageable oPageable, Long id_team) {
        oAuthService.OnlyAdmins();
        Page<ProjectEntity> oPage = null;
        if (id_team == null) {
            oPage = oProjectRepository.findAll(oPageable);
        } else {
            oPage = oProjectRepository.findByTeamId(id_team, oPageable);
        }
        return oPage;
    }

    public Long count() {
        oAuthService.OnlyAdmins();
        return oProjectRepository.count();
    }

    public Long update(ProjectEntity oProjectEntity) {
        validate(oProjectEntity.getId());
        oAuthService.OnlyAdmins();
        return oProjectRepository.save(oProjectEntity).getId();
    }

    public Long delete(Long id) {
        validate(id);
        oAuthService.OnlyAdmins();
        oProjectRepository.deleteById(id);
        return id;
    }

}
