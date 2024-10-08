package com.example.projekatmobilne.clients;


import com.example.projekatmobilne.model.Enum.NotificationType;
import com.example.projekatmobilne.model.Enum.RequestStatus;
import com.example.projekatmobilne.model.Enum.ReservationStatus;
import com.example.projekatmobilne.model.Notification;
import com.example.projekatmobilne.model.NotificationPreferences;
import com.example.projekatmobilne.model.requestDTO.AccommodationDTO;
import com.example.projekatmobilne.model.requestDTO.AuthenticationRequestDTO;
import com.example.projekatmobilne.model.requestDTO.AvailabilityDTO;
import com.example.projekatmobilne.model.requestDTO.ChangePasswordDTO;
import com.example.projekatmobilne.model.Enum.AccommodationType;
import com.example.projekatmobilne.model.Enum.Amenity;
import com.example.projekatmobilne.model.requestDTO.RegisterRequestDTO;
import com.example.projekatmobilne.model.requestDTO.ReservationDTO;
import com.example.projekatmobilne.model.requestDTO.UserDTO;
import com.example.projekatmobilne.model.responseDTO.MonthlyReportDTO;
import com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs.PageTypes.ReviewDTOPageItem;
import com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs.PageTypes.innerDTOPage.ReviewDTOPageItemInner;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("auth/authenticate")
    Call<ResponseBody> authenticate(@Body AuthenticationRequestDTO request);


    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("auth/register")
    Call<ResponseBody> register(@Body RegisterRequestDTO request);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("user")
    Call<ResponseBody> getUserData();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("user/change-info")
    Call<ResponseBody> changeUserData(@Body UserDTO request);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("user/change-password")
    Call<ResponseBody> changePassword(@Body ChangePasswordDTO request);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @DELETE("guest")
    Call<ResponseBody> deleteGuest();


    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @DELETE("owner")
    Call<ResponseBody> deleteOwner();


    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("auth/send-email-change-code")
    Call<ResponseBody> sendCodeEmail();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("auth/{email}/validate-code/{verification}")
    Call<ResponseBody> validateCode(@Path("verification") String verification, @Path("email") String email, @Body String newEmail);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("accommodations")
    Call<ResponseBody> getAccommodationsSearch(
            @Query("guestNum") Long guestNum,
            @Query("address") String address,
            @Query("startDate") LocalDate startDate,
            @Query("endDate") LocalDate endDate,
            @Query("amenities") List<Amenity> amenities,
            @Query("accommodationType") AccommodationType accommodationType,
            @Query("minPrice") Long minPrice,
            @Query("maxPrice") Long maxPrice,
            @Query("sortType") String sortType,
            @Query("isAscending") boolean isAscending,
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize
    );

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("accommodations/{accommodationId}/images/{imageId}")
    Call<ResponseBody> getAccommodationImage(@Path("accommodationId") Long accommodationId, @Path("imageId") Long imageId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("accommodation-requests/{accommodationRequestId}/images/{imageId}")
    Call<ResponseBody> getAccommodationRequestImage(@Path("accommodationRequestId") Long accommodationId, @Path("imageId") Long imageId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("accommodations/{accommodationId}")
    Call<ResponseBody> getAccommodation(@Path("accommodationId") Long accommodationId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("reviews/accommodations/{accommodationId}")
    Call<ResponseBody> getReviews(@Path("accommodationId") Long accommodationId,
                                  @Query("pageNo") int pageNo,
                                  @Query("pageSize") int pageSize);

    @Multipart
    @POST("accommodation-requests")
    Call<ResponseBody> createNewAccommodationRequest(@Part("accommodationDTO") AccommodationDTO accommodationDTO,
                                                     @Part List<MultipartBody.Part> images);

    @Multipart
    @POST("accommodation-requests/accommodations/{accommodationId}")
    Call<ResponseBody> createNewEditAccommodationRequest(@Part("accommodationDTO") AccommodationDTO accommodationDTO,
                                                     @Part List<MultipartBody.Part> images,
                                                         @Path("accommodationId") Long accommodationId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("accommodations/owner-list")
    Call<ResponseBody> getOwnerAccommodations(@Query("pageNo") int pageNo, @Query("pageSize") int pageSize);


    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("accommodation-requests")
    Call<ResponseBody> getOwnerAccommodationRequests(@Query("pageNo") int pageNo, @Query("pageSize") int pageSize);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("complaints/getUserComplaints")
    Call<ResponseBody> getUserReportsRequests(@Query("pageNo") int pageNo, @Query("pageSize") int pageSize);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("complaints/user-complaint/{complaintId}/{status}")
    Call<ResponseBody> reviewUserComplaint(@Path("complaintId") Long complaintId,
                                             @Path("status") RequestStatus status);
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("accommodation-requests/{accommodationRequestId}")
    Call<ResponseBody> getOwnerAccommodationRequest(@Path("accommodationRequestId") Long accommodationRequestId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("accommodation-requests/{accommodationRequestId}/{status}")
    Call<ResponseBody> processAccommodationRequest(@Path("accommodationRequestId") Long accommodationRequestId,
                                                   @Path("status") RequestStatus status,
                                                   @Body String reason);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("accommodations/{accommodationId}/reservation")
    Call<ResponseBody> createNewReservation(@Path("accommodationId") Long accommodationId, @Body ReservationDTO request);


    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("accommodations/reservations")
    Call<ResponseBody> getReservations(@Query("addressOrName") String addressOrName,
                                       @Query("minDate") LocalDate minDate,
                                       @Query("maxDate") LocalDate maxDate,
                                       @Query("reservationStatus") ReservationStatus reservationStatus,
                                       @Query("pageNo") int pageNo,
                                       @Query("pageSize") int pageSize);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("accommodations/{accommodationId}/reservations/{reservationId}")
    Call<ResponseBody> getReservationDetails(@Path("accommodationId") Long accommodationId,
                                             @Path("reservationId") Long reservationId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT(value = "accommodations/{accommodationId}/reservations/{reservationId}/{status}")
    Call<ResponseBody> processReservation(@Path("accommodationId") Long accommodationId,
                                          @Path("reservationId") Long reservationId,
                                          @Path("status") ReservationStatus status,
                                          @Body String reason);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("complaints/users/{reservationId}")
    Call<ResponseBody> sendHostReport(@Path("reservationId") Long reservationId,
                                      @Body String reason);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("accommodations/{accommodationId}/reservations/{reservationId}/conflict-reservations")
    Call<ResponseBody> getConflictReservations(@Path("accommodationId") Long accommodationId,
                                               @Path("reservationId") Long reservationId,
                                               @Query("pageNo") int pageNo,
                                               @Query("pageSize") int pageSize);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("accommodations/{accommodationId}/reservation-auto-accept/{status}")
    Call<ResponseBody> toggleAutoAccept(@Path("accommodationId") Long accommodationId,
                                        @Path("status") Boolean status);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("accommodations/{accommodationId}/reservation/{reservationId}/cancel")
    Call<ResponseBody> cancelReservation(@Path("accommodationId") Long accommodationId,
                                         @Path("reservationId") Long reservationId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @DELETE("accommodations/{accommodationId}/reservation/{reservationId}")
    Call<ResponseBody> deleteReservation(@Path("accommodationId") Long accommodationId,
                                         @Path("reservationId") Long reservationId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("accommodations/{accommodationId}/add-favorites")
    Call<ResponseBody> addFavorites(@Path("accommodationId") Long accommodationId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("accommodations/{accommodationId}/remove-favorites")
    Call<ResponseBody> removeFavorites(@Path("accommodationId") Long accommodationId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("accommodations/favorites")
    Call<ResponseBody> getFavorites(@Query("pageNo") int pageNo,
                                    @Query("pageSize") int pageSize);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("reviews/accommodations/{accommodationId}/reservations/{reservationId}")
    Call<ResponseBody> getReservationReview(@Path("accommodationId") Long accommodationId,
                                             @Path("reservationId") Long reservationId);


    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("reviews/accommodations/{accommodationId}/reservation/{reservationId}")
    Call<ResponseBody> createAccommodationReview(@Path("accommodationId") Long accommodationId,
                                                 @Path("reservationId") Long reservationId,
                                                 @Body ReviewDTOPageItemInner reviewDTO);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("reviews/owners/{ownerId}")
    Call<ResponseBody> createOwnerReview(@Path("ownerId") Long ownerId,
                                                 @Body ReviewDTOPageItemInner reviewDTO);


    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @DELETE("reviews/owner-reviews/{reviewId}")
    Call<ResponseBody> deleteOwnerReview(@Path("reviewId") Long reviewId);


    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @DELETE("reviews/accommodation-reviews/{reviewId}")
    Call<ResponseBody> deleteAccommodationReview(@Path("reviewId") Long reviewId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("complaints/reviews/{reviewId}")
    Call<ResponseBody> createReviewComplaint(@Path("reviewId") Long reviewId,
                                             @Body String reason);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("complaints/{complaintId}")
    Call<ResponseBody> getComplaint(@Path("complaintId") Long complaintId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("reports/last-12-months-report")
    Call<Map<String, MonthlyReportDTO>> getLast12MonthsReport(@Query("accommodationId") Long accommodationId);



    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("complaints")
    Call<ResponseBody> getComplaints(@Query("pageNo") int pageNo,
                                     @Query("pageSize") int pageSize);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("complaints/review-complaint/{complaintId}/{status}")
    Call<ResponseBody> processReviewCommentComplaint(@Path("complaintId") Long complaintId,
                                                     @Path("status") RequestStatus status,
                                                     @Body String response);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("notification/{id}/toggle-read")
    Call<ResponseBody> toggleNotification(@Path("id") Long id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("notification/user")
    Call<ResponseBody> getNotifications();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("/notification-preferences/{userId}/{notificationType}")
    Call<ResponseBody> updateNotificationPreference(
            @Path("userId") Long userId,
            @Path("notificationType") NotificationType notificationType,
            @Query("isEnabled") Boolean isEnabled);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("/notification-preferences/{userId}")
    Call<List<NotificationPreferences>> getNotificationPreferencesByUserId(@Path("userId") Long userId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("/notifications/{id}/toggle-read")
    Call<ResponseBody> toggleNotificationReadStatus(@Path("id") Long id);
  
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("/api/v1/fcm-token")
    Call<ResponseBody> saveFcmToken(@Query("userId") Long userId, @Query("token") String token);
   
}
