package com.pankaj.roi.services;

import com.pankaj.roi.models.PagingRequest;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
public class PagingService {

    private BlockingQueue<PagingRequest> queue = new ArrayBlockingQueue<>(1000);

    public void addPagingRequest(PagingRequest<?> request) throws InterruptedException {
        queue.put(request);
    }

    private void processPagingRequest() throws InterruptedException {
        while(true) {
            if(!queue.isEmpty()) {
                PagingRequest<?> request = queue.take();
//                request.getClass().equals()
            }
        }
    }
}
