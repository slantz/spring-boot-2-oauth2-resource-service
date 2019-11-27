package com.yourproject.resource.sample;

import com.yourproject.resource.model.mongo.Sample;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * {@link Sample} repository extending mongo repository with autogenerated method implementations.
 * Extends {@link SampleCustomRepository} as well.
 */
public interface SampleRepository extends MongoRepository<Sample, String>, SampleCustomRepository  {

    /**
     * Find {@link Sample} by username.
     *
     * @param username username.
     *
     * @return optional {@link Sample} list.
     */
    Optional<List<Sample>> findByUsername(String username);

    /**
     * Find {@link Sample} by username and {@link Sample} title.
     *
     * @param username username.
     * @param title {@link Sample} title.
     *
     * @return optional {@link Sample} list.
     */
    Optional<List<Sample>> findByUsernameAndTitle(String username, String title);

    /**
     * Find by username and precise date, like on the scheme below.
     *
        S'E'
        |-|

        SE
        |*|

     * @return optional {@link Sample} list.
     */
    @Query("{'username': ?0, 'date' : { '$gte' : ?1, '$lte' : ?1 } }")
    Optional<List<Sample>> findByUsernameAndPreciseDate(String username, Date date);

    /**
     * Find by username and inclusive date interval, like on the scheme below.
     *
     S'                     E'
     |----------------------|

        S                E
        |****************|

     * @return optional {@link Sample} list.
     */
    @Query("{'username': ?0, 'date' : { '$gte' : ?1 }, 'expiredDate' : { '$lte' : ?2 } }")
    Optional<List<Sample>> findByInclusiveDateRangeAndUsername(String username, Date startDate, Date expiredDate);

    /**
     * Find by username and overlapping date interval, like on the scheme below.
     *
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

     * @return optional {@link Sample} list.
     */
    @Query("{'username': ?0, 'date' : { '$lte' : ?2 }, 'expiredDate' : { '$gte' : ?1 } }")
    Optional<List<Sample>> findByOverlappingDateRangeAndUsername(String username, Date startDate, Date expiredDate);
}
