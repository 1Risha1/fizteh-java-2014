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
import ru.fizteh.java2.podorozhnaya.filemap.storeabledb.Table;
import ru.fizteh.java2.podorozhnaya.filemap.storeabledb.TableProvider;
import ru.fizteh.java2.podorozhnaya.filemap.storeabledb.TableProviderFactory;
import ru.fizteh.java2.podorozhnaya.filemap.utils.Types;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TableProviderTest {
    
    private static final String DATA_BASE_DIR = "./src/ru/fizteh/fivt/students/irinapodorozhnaya/test";
    private final File curDir = new File(DATA_BASE_DIR);
    private TableProvider provider;
    private List<Class<?>> list;

    @Autowired
    TableProviderFactory factory;
    
    @Before
    public void setUp() throws Exception {
        list  = new ArrayList<>();
        list.add(Integer.class);
        
        curDir.mkdirs();
        provider = factory.create(DATA_BASE_DIR);
        
    }
    
    @After
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(curDir);
    }

    @Test
    public void testCreateTable() throws Exception {
        Assert.assertNotNull(provider.createTable("createTable", list));
        Assert.assertNull(provider.createTable("createTable", list));
        provider.removeTable("createTable");
        Assert.assertNotNull(provider.createTable("createTable", list));
    }
    
    @Test
    public void testCreateSignature() throws Exception {
        
        list.add(Double.class);
        provider.createTable("createTable", list);        
        File sign = new File(new File(curDir, "createTable"), "signature.tsv");
        Assert.assertTrue(sign.isFile());
        
        Scanner sc = new Scanner(sign);
        List<Class<?>> columns = new ArrayList<>();
        
        while (sc.hasNext()) {
            columns.add(Types.getTypeByName(sc.next()));
        }
        sc.close();
        Assert.assertEquals(columns, list);
        
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNullList() throws Exception {
        provider.createTable("createTable", null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableIllegalType() throws Exception {
        list.add(provider.getClass());
        provider.createTable("createTable", list);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableIllegalSymbols() throws Exception {
        provider.createTable("%:^*(&^i", list);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableIllegalSymbols() throws Exception {
        provider.getTable("O*^//*"); 
    }

    @Test
    public void testGetCreateTableReference() throws Exception {
        Table table = provider.createTable("getCreateTableReference", list);
        Assert.assertNotNull(table);
        Assert.assertNotNull(provider.getTable("getCreateTableReference"));
        Assert.assertSame(provider.getTable("getCreateTableReference"), table);
        Assert.assertSame(provider.getTable("getCreateTableReference"),
                provider.getTable("getCreateTableReference"));
    }

    @Test
    public void testGetNonExistingTable() {
        Assert.assertNull(provider.getTable("nonExictingTable"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNullTable() throws Exception {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullTable() throws Exception {
        provider.createTable(null, list);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNullTable() throws Exception {
        provider.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveNotExistingTable() throws Exception {
        provider.removeTable("notExistingTable");
    }


    @Test
    public void toStringTest() {
        Assert.assertEquals(provider.toString(), "MyTableProvider[" + curDir.getAbsolutePath() + "]");
    }

    @Test
    public void doubleClose() throws Exception {
        provider.close();
        provider.close();
    }

    @Test(expected = IllegalStateException.class)
    public void createTableAfterClose() throws Exception {
        provider.close();
        provider.createTable("", null);
    }

    @Test(expected = IllegalStateException.class)
    public void getAfterClose() throws Exception {
        provider.close();
        provider.getTable("");
    }

    @Test(expected = IllegalStateException.class)
    public void removeAfterClose() throws Exception {
        provider.close();
        provider.removeTable("");
    }

    @Test(expected = IllegalStateException.class)
    public void createForAfterClose() throws Exception {
        provider.close();
        provider.createFor(null);
    }

    @Test
    public void getClosedTableShouldReturnNewTable() throws Exception {
        Table table = provider.createTable("table", list);
        table.close();
        Assert.assertNotSame(table, provider.getTable("table"));
    }
}
