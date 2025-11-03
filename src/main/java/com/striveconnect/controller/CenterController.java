	package com.striveconnect.controller;
	
	import com.striveconnect.dto.CenterDto;
	import com.striveconnect.service.CenterService;
	import org.springframework.http.ResponseEntity;
	import org.springframework.web.bind.annotation.GetMapping;
	import org.springframework.web.bind.annotation.PathVariable;
	import org.springframework.web.bind.annotation.RequestMapping;
	import org.springframework.web.bind.annotation.RequestParam;
	import org.springframework.web.bind.annotation.RestController;
	
	import java.util.List;
	
	@RestController
	@RequestMapping("/api/centers")
	public class CenterController {
	
	    private final CenterService centerService;
	
	    public CenterController(CenterService centerService) {
	        this.centerService = centerService;
	    }
	
	    /**
	     * GET /api/centers
	     * Retrieves all centers for the tenant associated with the authenticated user.
	     *
	     * @return A list of centers.
	     */
	    @GetMapping("/tenantId/{tenantId}")
	    public ResponseEntity<List<CenterDto>> getTenantCenters(@PathVariable String tenantId) {
	        List<CenterDto> centers = centerService.getCentersByTenant(tenantId);
	        return ResponseEntity.ok(centers);
	    }
	}
