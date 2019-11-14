package com.yourproject.resource.sample;

import com.yourproject.resource.model.mongo.Sample;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface SampleRepository extends MongoRepository<Sample, String>, SampleCustomRepository  {

    Optional<List<Sample>> findByIdIn(Collection<String> ids);

    Optional<List<Sample>> findByUsername(String username);

    @Query("{'username': ?0, 'date' : { '$gte' : ?1, '$lte' : ?2 } }")
    Optional<List<Sample>> findByUsernameAndDate(String username, Date startDate, Date endDate);

    @Query("{'date' : { '$gte' : ?0 }, 'expiredDate' : { '$lte' : ?1 }, 'username': ?2 }")
    Optional<List<Sample>> findByInclusiveDateRangeAndUsername(Date startDate, Date expiredDate, String username);

    /**
     S'                   E'
     |--------------------|

     S                E
     |****************|

     S'          E'
     |-----------|

     S'          E'
     |-----------|

     S'          E'
     |-----------|
     */
    @Query("{'date' : { '$lte' : ?1 }, 'expiredDate' : { '$gte' : ?0 }, 'username': ?2 }")
    Optional<List<Sample>> findByOverlappingDateRangeAndUsername(Date startDate, Date expiredDate, String username);
}
