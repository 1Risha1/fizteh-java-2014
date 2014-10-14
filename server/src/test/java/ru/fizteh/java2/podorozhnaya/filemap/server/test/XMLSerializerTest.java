package ru.fizteh.java2.podorozhnaya.filemap.server.test;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.fizteh.java2.podorozhnaya.filemap.server.AppConfig;
import ru.fizteh.java2.podorozhnaya.filemap.storeabledb.ColumnFormatException;
import ru.fizteh.java2.podorozhnaya.filemap.storeabledb.Storeable;
import ru.fizteh.java2.podorozhnaya.filemap.storeabledb.Table;
import ru.fizteh.java2.podorozhnaya.filemap.storeabledb.TableProvider;
import ru.fizteh.java2.podorozhnaya.filemap.storeabledbimpl.MyStoreable;
import ru.fizteh.java2.podorozhnaya.filemap.storeabledbimpl.MyTableProvider;
import ru.fizteh.java2.podorozhnaya.filemap.storeabledbimpl.XMLSerializer;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class XMLSerializerTest {

    
    private Storeable s;
    private List<Class<?>> columnTypes;
    private String desirialized = "<row><col>Hello</col><col>5</col></row>";
    private static final String DATA_BASE_DIR = "./src/ru/fizteh/fivt/students/irinapodorozhnaya/test";
    private File f = new File(DATA_BASE_DIR);

    private TableProvider provider;
    private Table table;
    
    @Before
    public void setUp() throws Exception {
        f.mkdirs();
        provider = new MyTableProvider(f);
        columnTypes = new ArrayList<>();
        columnTypes.add(String.class);
        columnTypes.add(Integer.class);
        table = provider.createTable("table", columnTypes);        
    }
    
    @After
    public void tearDown() throws Exception {
        provider.removeTable("table");
        FileUtils.deleteDirectory(f);
    }
    
    @Test
    public void correctDeserialize() throws Exception {
        Storeable s = XMLSerializer.deserialize(table, desirialized);
        Assert.assertEquals("Hello", s.getStringAt(0));
        Assert.assertEquals(new Integer(5), s.getIntAt(1));
    }
    
    @Test(expected = XMLStreamException.class)
    public void incorrectDeserialize() throws Exception {
        XMLSerializer.deserialize(table, "<row>fvdfv");
    }
    
    @Test(expected = ParseException.class)
    public void incorrectTypeDeserialize() throws Exception {
        XMLSerializer.deserialize(table, "<row><col>Hello</col><col>jghd</col></row>");
    }

    @Test
    public void correctSerialize() throws Exception {
        s = new MyStoreable(table);
        s.setColumnAt(0, "Hello");
        s.setColumnAt(1, 5);
        String res = XMLSerializer.serialize(table, s);
        Assert.assertEquals(res, desirialized);
    }
    
    @Test(expected = ColumnFormatException.class)
    public void incorrectSerialize() throws Exception {
        s = new MyStoreable(table);
        s.setColumnAt(0, "Hello");
        s.setColumnAt(1, 5);
        columnTypes.add(Byte.class);
        Table t = provider.createTable("table2", columnTypes);
        XMLSerializer.serialize(t, s);
    }

    @Test
    public void serializeWithNull() {
        Storeable s = provider.createFor(table);
        Assert.assertEquals(provider.serialize(table, s), "<row><null/><null/></row>");
    }

    @Test
    public void deserializeWithNull() throws Exception {
        Storeable s = provider.deserialize(table, "<row><null/><null/></row>");
        Assert.assertEquals(s, provider.createFor(table));
    }
}
