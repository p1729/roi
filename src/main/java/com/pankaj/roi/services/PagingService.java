package com.pankaj.roi.services;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pankaj.roi.models.FBPhotos;
import com.pankaj.roi.models.PagingRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PagingService {

	@Autowired
	private APIClient apiClient;

	@Autowired
	private UserService service;

//	public void processPagingRequest(PagingRequest<?> request) throws InterruptedException {
//		CompletableFuture.runAsync(() -> {
//			try {
//				if (request.getClazz() == FBPhotos.class) {
//					FBPhotos userPhotoDetails = apiClient.getNextPage((PagingRequest<FBPhotos>) request);
//					service.loadUserPhotos(request.getPersistedUser(), userPhotoDetails);
//				}
//			} catch(Exception e) {
//				LOG.error("Error occured while asking for next page {} ", e);
//			}
//		});
//	}
}
