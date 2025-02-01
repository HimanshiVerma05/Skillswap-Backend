package com.dal.skillswap.controller;

import com.dal.skillswap.models.request.ReviewRequest;
import com.dal.skillswap.models.response.ReviewResponse;
import com.dal.skillswap.service.FileStoreService;
import com.dal.skillswap.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static com.dal.skillswap.constants.PathConstants.REVIEW_IMAGE_UPLOAD_DIR;

@RestController
@RequestMapping("/review")
@Slf4j
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    FileStoreService fileStoreService;

    @PostMapping(path = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> createReview(@RequestPart ReviewRequest reviewRequest, @RequestPart Optional<MultipartFile> reviewImage) {

        if (reviewImage.isPresent()) {
            String uploadedFileName = fileStoreService.store(reviewImage.get(), REVIEW_IMAGE_UPLOAD_DIR);

            if (uploadedFileName == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to store image file");
            }
            reviewRequest.setReviewImageName(uploadedFileName);
        }
        reviewService.createReview(reviewRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body("Review created successfully");
    }

    @GetMapping("/{revieweeId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByReviewee(@PathVariable Long revieweeId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByReviewee(revieweeId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/reviewImage/{filename:.+}")
    public ResponseEntity<Resource> getReviewImage(@PathVariable String filename) {
        Resource file = fileStoreService.load(filename, REVIEW_IMAGE_UPLOAD_DIR);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {
        boolean deleted = reviewService.deleteReview(reviewId);
        if (deleted) {
            return ResponseEntity.ok("Review deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found");
        }
    }
}
