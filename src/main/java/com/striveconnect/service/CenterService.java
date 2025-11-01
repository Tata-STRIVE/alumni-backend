package com.striveconnect.service;

import com.striveconnect.dto.CenterDto;
import com.striveconnect.entity.Center;
import com.striveconnect.repository.CenterRepository;
import com.striveconnect.util.TenantContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CenterService {

    private final CenterRepository centerRepository;

    public CenterService(CenterRepository centerRepository) {
        this.centerRepository = centerRepository;
    }

    /**
     * Retrieves all centers for the current tenant.
     * The tenant ID is retrieved from the TenantContext.
     *
     * @return A list of CenterDto objects.
     */
    public List<CenterDto> getCentersByTenant(String tenantId) {
    	
    	if(tenantId==null)
         tenantId = TenantContext.getCurrentTenant();
        
        // Use the repository to find centers by tenant
        List<Center> centers = centerRepository.findByTenantId(tenantId);
        
        // Convert the list of entities to a list of DTOs
        return centers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Converts a Center entity to a CenterDto.
     *
     * @param center The Center entity.
     * @return The corresponding CenterDto.
     */
    private CenterDto convertToDto(Center center) {
        CenterDto dto = new CenterDto();
        dto.setCenterId(center.getCenterId());
        dto.setName(center.getName());
        dto.setCity(center.getCity());
        return dto;
    }
}
