package com.yourproject.resource.sample;

import com.yourproject.resource.model.mongo.Sample;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface SampleRepository extends MongoRepository<Sample, String>, SampleCustomRepository  {

    Optional<List<Sample>> findByUsername(String username);

    /**
        S'E'
        |-|

        SE
        |*|
     */
    @Query("{'username': ?0, 'date' : { '$gte' : ?1, '$lte' : ?1 } }")
    Optional<List<Sample>> findByUsernameAndPreciseDate(String username, Date date);

    /**
     S'                     E'
     |----------------------|

        S                E
        |****************|
     */
    @Query("{'username': ?0, 'date' : { '$gte' : ?1 }, 'expiredDate' : { '$lte' : ?2 } }")
    Optional<List<Sample>> findByInclusiveDateRangeAndUsername(String username, Date startDate, Date expiredDate);

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
    @Query("{'username': ?0, 'date' : { '$lte' : ?2 }, 'expiredDate' : { '$gte' : ?1 } }")
    Optional<List<Sample>> findByOverlappingDateRangeAndUsername(String username, Date startDate, Date expiredDate);
}
