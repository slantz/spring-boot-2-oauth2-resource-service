package com.yourproject.resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
//@SpringBootTest
//@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
//@DataMongoTest
public class ApplicationTests {

//	@Autowired
//	private MongoTemplate mongoTemplate;

//	@Before
//	public void setUp() {
//		categoryRepository.deleteAll();
//	}

//	@Test
//	public void findById() {
//		Category category = new Category("Lalka");
//		categoryRepository.save(category);
//
//		Category foundCategory = categoryRepository.findByType("Lalka").get();
//
//		assertNotNull(foundCategory);
//	}

//    @Test
//    public void aggregateCategorys() {
//        saveCategorys();
//
//        List<Warehouse> warehouseSummaries = categoryRepository.aggregate(5.0f, 70.0f);
//
//        assertEquals(3, warehouseSummaries.size());
//        Warehouse liverpoolCategorys = getLiverpoolCategorys(warehouseSummaries);
//        assertEquals(39.1, liverpoolCategorys.getTotalRevenue(), 0.01);
//        assertEquals(19.55, liverpoolCategorys.getAveragePrice(), 0.01);
//    }

//    private void saveCategorys() {
//        categoryRepository.save(new Category("Norwhich", "NW1", 3.0f));
//        categoryRepository.save(new Category("London", "LN1", 25.0f));
//        categoryRepository.save(new Category("London", "LN2", 35.0f));
//        categoryRepository.save(new Category("Liverpool", "LV1", 15.2f));
//        categoryRepository.save(new Category("Macnhester", "MN1", 45.5f));
//        categoryRepository.save(new Category("Liverpool", "LV2", 23.9f));
//        categoryRepository.save(new Category("London", "LN3", 55.5f));
//        categoryRepository.save(new Category("Leeds", "LD1", 87.0f));
//    }

//    private Warehouse getLiverpoolCategorys(List<Warehouse> warehouseSummaries) {
//        return warehouseSummaries.stream().filter(product -> "Liverpool".equals(product.getFirstName())).findAny().get();
//    }

//	@After
//    public void after() {
//        categoryRepository.saveAll(
//                Arrays.asList(
//                        new Category("Palka"),
//                        new Category("Strelyalka"),
//                        new Category("Kurtka"),
//                        new Category("Otrehutka")
//
//                )
//        );
//
//        categoryRepository.save(new Category("beefvelington"));
//
//		Category beefvelington = categoryRepository.findByType("beefvelington").get();
//
//		categoryRepository.save(new Category("Orio", Arrays.asList(beefvelington)));
//    }

	@Test
	public void contextLoads() {}
}