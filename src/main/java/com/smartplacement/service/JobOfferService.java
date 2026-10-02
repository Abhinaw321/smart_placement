package com.smartplacement.service;

import com.smartplacement.dto.common.PagedResponse;
import com.smartplacement.dto.offer.JobOfferResponseDto;
import com.smartplacement.dto.offer.OfferIssueRequestDto;
import com.smartplacement.dto.offer.OfferResponseRequestDto;
import com.smartplacement.security.UserPrincipal;

/**
 * Service contract managing the issuance, candidate decision response, revocation,
 * and placement confirmation of job offers.
 */
public interface JobOfferService {

    JobOfferResponseDto issueOffer(OfferIssueRequestDto request, UserPrincipal currentUser);

    JobOfferResponseDto respondToOffer(Long offerId, OfferResponseRequestDto request, UserPrincipal currentUser);

    JobOfferResponseDto revokeOffer(Long offerId, String reason, UserPrincipal currentUser);

    PagedResponse<JobOfferResponseDto> getMyOffers(UserPrincipal currentUser, int page, int size);

    PagedResponse<JobOfferResponseDto> getOffersByJob(Long jobId, int page, int size, UserPrincipal currentUser);

    JobOfferResponseDto getOfferById(Long offerId, UserPrincipal currentUser);
}
