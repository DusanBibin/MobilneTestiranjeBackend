package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.enums.AccommodationType;
import com.example.mobilnetestiranjebackend.enums.Amenity;
import com.example.mobilnetestiranjebackend.model.Accommodation;
import com.example.mobilnetestiranjebackend.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    Optional<Accommodation> findAccommodationsByOwnerAndName(Owner owner, String name);

    Optional<Accommodation> findAccommodationById(Long accommodationId);


    @Query("select a from Accommodation a where a.owner.id = :ownerId")
    List<Accommodation> findByOwnerId(Long ownerId);


    @Query("select a from Guest g join g.favorites a where g.id = :guestId and a.id = :accommodationId ")
    Optional<Accommodation> findFavoritesByAccommodationIdAndGuestId(Long accommodationId, Long guestId);

    @Query("select distinct a from Accommodation a join Availability av join a.amenities am where ((:guestNum between a.minGuests and a.maxGuests) and" +
            " (lower(a.address) like LOWER(CONCAT('%', :address, '%'))) and" +
            " ((:startDate between av.startDate and av.endDate) or (:endDate between av.startDate and av.endDate)" +
            "  or (:startDate < av.startDate and :endDate > av.endDate)) and" +
            " (:accommodationType is null or a.accommodationType = :accommodationType) and" +
            " (:minPrice is null or " +
            "     (case when av.pricePerGuest = true then :daysBetween * av.price * :guestNum else :daysBetween * av.price end) >= :minPrice) and" +
            " (:maxPrice is null or " +
            "     (case when av.pricePerGuest = true then :daysBetween * av.price * :guestNum else :daysBetween * av.price end) <= :maxPrice))")
    List<Accommodation> searchAccommodations(Long guestNum, String address, LocalDate startDate, LocalDate endDate,
                                             AccommodationType accommodationType, Long minPrice, Long maxPrice, Long daysBetween);
}
