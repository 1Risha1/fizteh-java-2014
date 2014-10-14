package ru.fizteh.java2.podorozhnaya.filemap.server.test;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.fizteh.java2.podorozhnaya.filemap.server.AppConfig;
import ru.fizteh.java2.podorozhnaya.filemap.storeabledbimpl.MyTableProviderFactory;

import java.io.File;
import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TableProviderFactoryTest {
    @Autowired
    private MyTableProviderFactory factory;

    private static final String DATA_BASE_DIR = "./src/ru/fizteh/fivt/students/irinapodorozhnaya/test";
    private final File curDir = new File(DATA_BASE_DIR);

    @Before
    public void setUp() throws Exception {
        curDir.mkdirs();
    }
    
    @After
    public void tearDown () throws IOException {
         FileUtils.deleteDirectory(curDir);

    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() throws Exception {
        factory.create(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateIllegal() throws Exception {
        factory.create("%*&YIH&?");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateFile() throws Exception {
        File file = new File(curDir, "file");
        file.createNewFile();
        factory.create(file.getAbsolutePath());
    }
    
    @Test(expected = IOException.class)
    public void testCreateNonExisting() throws Exception {
        File file = new File(curDir, "non-existing-file");
        factory.create(file.getName());
    }

    @Test
    public void testCreateLegal() throws Exception {
        Assert.assertNotNull(factory.create(DATA_BASE_DIR));
    }

    @Test
    public void doubleClose() throws Exception {
        factory.close();
        factory.close();
    }

    @Test(expected = IllegalStateException.class)
    public void createAfterClose() throws Exception {
        factory.close();
        factory.create(DATA_BASE_DIR);
    }
}
