package com.elepy.hibernate.fast;

import com.elepy.hibernate.config.DatabaseConfigurations;
import com.elepy.tests.basic.BasicFuntionalityTest;


public class DerbyBasicFuntionalityTest extends BasicFuntionalityTest {
    public DerbyBasicFuntionalityTest() {
        super(DatabaseConfigurations.ApacheDerby);
    }

}