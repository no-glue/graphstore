package org.gephi.graph.store;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import org.gephi.attribute.api.Origin;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author mbastian
 */
public class SerializationTest {

    @Test
    public void testEdgeStoreMixed() throws IOException, ClassNotFoundException {
        GraphStore graphStore = new GraphStore();

        NodeStore nodeStore = graphStore.nodeStore;
        EdgeStore edgeStore = graphStore.edgeStore;
        NodeImpl[] nodes = GraphGenerator.generateNodeList(5100);
        nodeStore.addAll(Arrays.asList(nodes));
        EdgeImpl[] edges = GraphGenerator.generateMixedEdgeList(nodeStore, 9000, 0, true);
        edgeStore.addAll(Arrays.asList(edges));

        Serialization ser = new Serialization(graphStore);
        byte[] buf = ser.serialize(graphStore);
        graphStore.clear();

        GraphStore l = (GraphStore) ser.deserialize(buf);
        Assert.assertTrue(nodeStore.equals(l.nodeStore));
        Assert.assertTrue(edgeStore.equals(l.edgeStore));
    }

    @Test
    public void testEdgeStoreMultipleTypes() throws IOException, ClassNotFoundException {
        GraphStore graphStore = new GraphStore();

        NodeStore nodeStore = graphStore.nodeStore;
        EdgeStore edgeStore = graphStore.edgeStore;
        NodeImpl[] nodes = GraphGenerator.generateNodeList(5100);
        nodeStore.addAll(Arrays.asList(nodes));
        EdgeImpl[] edges = GraphGenerator.generateMultiTypeEdgeList(nodeStore, 9000, 3, true, true);
        edgeStore.addAll(Arrays.asList(edges));

        Serialization ser = new Serialization(graphStore);
        byte[] buf = ser.serialize(graphStore);
        graphStore.clear();

        GraphStore l = (GraphStore) ser.deserialize(buf);
        Assert.assertTrue(nodeStore.equals(l.nodeStore));
        Assert.assertTrue(edgeStore.equals(l.edgeStore));
    }

    @Test
    public void testEdgeStore() throws IOException, ClassNotFoundException {
        GraphStore graphStore = new GraphStore();

        NodeStore nodeStore = graphStore.nodeStore;
        EdgeStore edgeStore = graphStore.edgeStore;
        NodeImpl[] nodes = GraphGenerator.generateSmallNodeList();
        nodeStore.addAll(Arrays.asList(nodes));
        EdgeImpl[] edges = GraphGenerator.generateSmallEdgeList();
        edgeStore.addAll(Arrays.asList(edges));

        Serialization ser = new Serialization(graphStore);
        byte[] buf = ser.serialize(graphStore);
        graphStore.clear();

        GraphStore l = (GraphStore) ser.deserialize(buf);
        Assert.assertTrue(nodeStore.equals(l.nodeStore));
        Assert.assertTrue(edgeStore.equals(l.edgeStore));
    }

    @Test
    public void testNodeStore() throws IOException, ClassNotFoundException {
        GraphStore graphStore = new GraphStore();

        NodeStore nodeStore = graphStore.nodeStore;
        EdgeStore edgeStore = graphStore.edgeStore;
        NodeImpl[] nodes = GraphGenerator.generateSmallNodeList();
        nodeStore.addAll(Arrays.asList(nodes));

        Serialization ser = new Serialization(graphStore);
        byte[] buf = ser.serialize(graphStore);

        graphStore = new GraphStore();
        ser = new Serialization(graphStore);
        GraphStore l = (GraphStore) ser.deserialize(buf);
        Assert.assertTrue(nodeStore.equals(l.nodeStore));
        Assert.assertTrue(edgeStore.equals(l.edgeStore));
    }

    @Test
    public void testNode() throws IOException, ClassNotFoundException {
        GraphStore graphStore = new GraphStore();
        ColumnStore columnStore = graphStore.nodePropertyStore;
        ColumnImpl col1 = new ColumnImpl("0", Integer.class, "title", 8, Origin.DATA, false);
        ColumnImpl col2 = new ColumnImpl("1", String.class, null, "default", Origin.PROPERTY, false);
        ColumnImpl col3 = new ColumnImpl("2", int[].class, null, null, Origin.PROPERTY, false);
        columnStore.addColumn(col1);
        columnStore.addColumn(col2);
        columnStore.addColumn(col3);

        NodeImpl node = new NodeImpl("Foo", graphStore);
        node.setProperty(col1, 1);
        node.setProperty(col3, new int[]{1, 7, 3, 4});

        Serialization ser = new Serialization(graphStore);
        byte[] buf = ser.serialize(node);
        
        graphStore = new GraphStore();
        ser = new Serialization(graphStore);
        NodeImpl l = (NodeImpl) ser.deserialize(buf);
        Assert.assertTrue(node.equals(l));
        Assert.assertTrue(Arrays.deepEquals(l.properties, node.properties));
    }

    @Test
    public void testTimestampStore() throws IOException, ClassNotFoundException {
        GraphStore graphStore = new GraphStore();

        TimestampStore timestampStore = graphStore.timestampStore;
        timestampStore.addTimestamp(1.0);
        timestampStore.addTimestamp(2.0);
        timestampStore.addTimestamp(3.0);

        timestampStore.removeTimestamp(1.0);

        Serialization ser = new Serialization(graphStore);
        byte[] buf = ser.serialize(timestampStore);

        graphStore = new GraphStore();
        ser = new Serialization(graphStore);
        TimestampStore l = (TimestampStore) ser.deserialize(buf);
        Assert.assertEquals(timestampStore, l);
    }

    @Test
    public void testEdgeTypeStore() throws IOException, ClassNotFoundException {
        GraphStore graphStore = new GraphStore();

        EdgeTypeStore edgeTypeStore = graphStore.edgeTypeStore;
        edgeTypeStore.addType("Foo");
        edgeTypeStore.addType(8);
        edgeTypeStore.addType("Bar");

        edgeTypeStore.removeType("Foo");
        Serialization ser = new Serialization(graphStore);
        byte[] buf = ser.serialize(edgeTypeStore);

        graphStore = new GraphStore();
        ser = new Serialization(graphStore);
        EdgeTypeStore l = (EdgeTypeStore) ser.deserialize(buf);
        Assert.assertTrue(edgeTypeStore.equals(l));
    }

    @Test
    public void testColumnStore() throws IOException, ClassNotFoundException {
        GraphStore graphStore = new GraphStore();

        ColumnStore columnStore = graphStore.nodePropertyStore;
        ColumnImpl col1 = new ColumnImpl("0", Integer.class, "title", 8, Origin.DATA, false);
        ColumnImpl col2 = new ColumnImpl("1", String.class, null, "default", Origin.PROPERTY, false);
        ColumnImpl col3 = new ColumnImpl("2", int[].class, null, null, Origin.PROPERTY, false);
        columnStore.addColumn(col1);
        columnStore.addColumn(col2);
        columnStore.addColumn(col3);
        columnStore.removeColumn(col1);

        Serialization ser = new Serialization(graphStore);
        byte[] buf = ser.serialize(columnStore);

        graphStore = new GraphStore();
        ser = new Serialization(graphStore);
        ColumnStore l = (ColumnStore) ser.deserialize(buf);
        Assert.assertTrue(columnStore.equals(l));
    }

    @Test
    public void testColumn() throws IOException, ClassNotFoundException {
        ColumnImpl col = new ColumnImpl("0", Integer.class, "title", 8, Origin.DATA, false);

        Serialization ser = new Serialization(null);
        byte[] buf = ser.serialize(col);
        ColumnImpl l = (ColumnImpl) ser.deserialize(buf);
        Assert.assertEquals(col, l);

        Assert.assertEquals(l.defaultValue, col.getDefaultValue());
        Assert.assertEquals(l.indexed, col.isIndexed());
        Assert.assertEquals(l.origin, col.getOrigin());
        Assert.assertEquals(l.title, col.getTitle());
        Assert.assertEquals(l.storeId, col.getStoreId());
    }

    @Test
    public void testInt() throws IOException, ClassNotFoundException {
        Serialization ser = new Serialization(null);
        int[] vals = {
            Integer.MIN_VALUE,
            -Short.MIN_VALUE * 2,
            -Short.MIN_VALUE + 1,
            -Short.MIN_VALUE,
            -10, -9, -8, -7, -6, -5, -4, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
            127, 254, 255, 256, Short.MAX_VALUE, Short.MAX_VALUE + 1,
            Short.MAX_VALUE * 2, Integer.MAX_VALUE
        };
        for (int i : vals) {
            byte[] buf = ser.serialize(i);
            Object l2 = ser.deserialize(buf);
            Assert.assertTrue(l2.getClass() == Integer.class);
            Assert.assertEquals(l2, i);
        }
    }

    @Test
    public void testShort() throws IOException, ClassNotFoundException {
        Serialization ser = new Serialization(null);
        short[] vals = {
            (short) (-Short.MIN_VALUE + 1),
            (short) -Short.MIN_VALUE,
            -10, -9, -8, -7, -6, -5, -4, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
            127, 254, 255, 256, Short.MAX_VALUE, Short.MAX_VALUE - 1,
            Short.MAX_VALUE
        };
        for (short i : vals) {
            byte[] buf = ser.serialize(i);
            Object l2 = ser.deserialize(buf);
            Assert.assertTrue(l2.getClass() == Short.class);
            Assert.assertEquals(l2, i);
        }
    }

    @Test
    public void testDouble() throws IOException, ClassNotFoundException {
        Serialization ser = new Serialization(null);
        double[] vals = {
            1f, 0f, -1f, Math.PI, 255, 256, Short.MAX_VALUE, Short.MAX_VALUE + 1, -100
        };
        for (double i : vals) {
            byte[] buf = ser.serialize(i);
            Object l2 = ser.deserialize(buf);
            Assert.assertTrue(l2.getClass() == Double.class);
            Assert.assertEquals(l2, i);
        }
    }

    @Test
    public void testFloat() throws IOException, ClassNotFoundException {
        Serialization ser = new Serialization(null);
        float[] vals = {
            1f, 0f, -1f, (float) Math.PI, 255, 256, Short.MAX_VALUE, Short.MAX_VALUE + 1, -100
        };
        for (float i : vals) {
            byte[] buf = ser.serialize(i);
            Object l2 = ser.deserialize(buf);
            Assert.assertTrue(l2.getClass() == Float.class);
            Assert.assertEquals(l2, i);
        }
    }

    @Test
    public void testChar() throws IOException, ClassNotFoundException {
        Serialization ser = new Serialization(null);
        char[] vals = {
            'a', ' '
        };
        for (char i : vals) {
            byte[] buf = ser.serialize(i);
            Object l2 = ser.deserialize(buf);
            Assert.assertTrue(l2.getClass() == Character.class);
            Assert.assertEquals(l2, i);
        }
    }

    @Test
    public void testLong() throws IOException, ClassNotFoundException {
        Serialization ser = new Serialization(null);
        long[] vals = {
            Long.MIN_VALUE,
            Integer.MIN_VALUE, Integer.MIN_VALUE - 1, Integer.MIN_VALUE + 1,
            -Short.MIN_VALUE * 2,
            -Short.MIN_VALUE + 1,
            -Short.MIN_VALUE,
            -10, -9, -8, -7, -6, -5, -4, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
            127, 254, 255, 256, Short.MAX_VALUE, Short.MAX_VALUE + 1,
            Short.MAX_VALUE * 2, Integer.MAX_VALUE, Integer.MAX_VALUE + 1, Long.MAX_VALUE
        };
        for (long i : vals) {
            byte[] buf = ser.serialize(i);
            Object l2 = ser.deserialize(buf);
            Assert.assertTrue(l2.getClass() == Long.class);
            Assert.assertEquals(l2, i);
        }
    }

    @Test
    public void testBoolean1() throws IOException, ClassNotFoundException {
        Serialization ser = new Serialization(null);
        byte[] buf = ser.serialize(true);
        Object l2 = ser.deserialize(buf);
        Assert.assertTrue(l2.getClass() == Boolean.class);
        Assert.assertEquals(l2, true);

        byte[] buf2 = ser.serialize(false);
        Object l22 = ser.deserialize(buf2);
        Assert.assertTrue(l22.getClass() == Boolean.class);
        Assert.assertEquals(l22, false);

    }

    @Test
    public void testString() throws IOException, ClassNotFoundException {
        Serialization ser = new Serialization(null);
        byte[] buf = ser.serialize("Abcd");
        String l2 = (String) ser.deserialize(buf);
        Assert.assertEquals(l2, "Abcd");
    }

    @Test
    public void testBigString() throws IOException, ClassNotFoundException {
        Serialization ser = new Serialization(null);
        String bigString = "";
        for (int i = 0; i < 1e4; i++) {
            bigString += i % 10;
        }
        byte[] buf = ser.serialize(bigString);
        String l2 = (String) ser.deserialize(buf);
        Assert.assertEquals(l2, bigString);
    }

    @Test
    public void testClass() throws IOException, ClassNotFoundException {
        Serialization ser = new Serialization(null);
        byte[] buf = ser.serialize(String.class);
        Class l2 = (Class) ser.deserialize(buf);
        Assert.assertEquals(l2, String.class);
    }

    @Test
    public void testClass2() throws IOException, ClassNotFoundException {
        Serialization ser = new Serialization(null);
        byte[] buf = ser.serialize(long[].class);
        Class l2 = (Class) ser.deserialize(buf);
        Assert.assertEquals(l2, long[].class);
    }

    @Test
    public void testUnicodeString() throws ClassNotFoundException, IOException {
        Serialization ser = new Serialization(null);
        String s = "Ciudad Bolíva";
        byte[] buf = ser.serialize(s);
        Object l2 = ser.deserialize(buf);
        Assert.assertEquals(l2, s);
    }

    @Test
    public void testNegativeLongsArray() throws ClassNotFoundException, IOException {
        Serialization ser = new Serialization(null);
        long[] l = new long[]{-12};
        Object deserialize = ser.deserialize(ser.serialize(l));
        Assert.assertTrue(Arrays.equals(l, (long[]) deserialize));
    }

    @Test
    public void testNegativeIntArray() throws ClassNotFoundException, IOException {
        Serialization ser = new Serialization(null);
        int[] l = new int[]{-12};
        Object deserialize = ser.deserialize(ser.serialize(l));
        Assert.assertTrue(Arrays.equals(l, (int[]) deserialize));
    }

    @Test
    public void testNegativeShortArray() throws ClassNotFoundException, IOException {
        Serialization ser = new Serialization(null);
        short[] l = new short[]{-12};
        Object deserialize = ser.deserialize(ser.serialize(l));
        Assert.assertTrue(Arrays.equals(l, (short[]) deserialize));
    }

    @Test
    public void testBooleanArray() throws ClassNotFoundException, IOException {
        Serialization ser = new Serialization(null);
        boolean[] l = new boolean[]{true, false};
        Object deserialize = ser.deserialize(ser.serialize(l));
        Assert.assertTrue(Arrays.equals(l, (boolean[]) deserialize));
    }

    @Test
    public void testDoubleArray() throws ClassNotFoundException, IOException {
        Serialization ser = new Serialization(null);
        double[] l = new double[]{Math.PI, 1D};
        Object deserialize = ser.deserialize(ser.serialize(l));
        Assert.assertTrue(Arrays.equals(l, (double[]) deserialize));
    }

    @Test
    public void testFloatArray() throws ClassNotFoundException, IOException {
        Serialization ser = new Serialization(null);
        float[] l = new float[]{1F, 1.234235F};
        Object deserialize = ser.deserialize(ser.serialize(l));
        Assert.assertTrue(Arrays.equals(l, (float[]) deserialize));
    }

    @Test
    public void testByteArray() throws ClassNotFoundException, IOException {
        Serialization ser = new Serialization(null);
        byte[] l = new byte[]{1, 34, -5};
        Object deserialize = ser.deserialize(ser.serialize(l));
        Assert.assertTrue(Arrays.equals(l, (byte[]) deserialize));
    }

    @Test
    public void testCharArray() throws ClassNotFoundException, IOException {
        Serialization ser = new Serialization(null);
        char[] l = new char[]{'1', 'a', '&'};
        Object deserialize = ser.deserialize(ser.serialize(l));
        Assert.assertTrue(Arrays.equals(l, (char[]) deserialize));
    }

    @Test
    public void testDate() throws IOException, ClassNotFoundException {
        Serialization ser = new Serialization(null);
        Date d = new Date(6546565565656L);
        Assert.assertEquals(d, ser.deserialize(ser.serialize(d)));
        d = new Date(System.currentTimeMillis());
        Assert.assertEquals(d, ser.deserialize(ser.serialize(d)));
    }

    @Test
    public void testBigDecimal() throws IOException, ClassNotFoundException {
        Serialization ser = new Serialization(null);
        BigDecimal d = new BigDecimal("445656.7889889895165654423236");
        Assert.assertEquals(d, ser.deserialize(ser.serialize(d)));
        d = new BigDecimal("-53534534534534445656.7889889895165654423236");
        Assert.assertEquals(d, ser.deserialize(ser.serialize(d)));
    }

    @Test
    public void testBigInteger() throws IOException, ClassNotFoundException {
        Serialization ser = new Serialization(null);
        BigInteger d = new BigInteger("4456567889889895165654423236");
        Assert.assertEquals(d, ser.deserialize(ser.serialize(d)));
        d = new BigInteger("-535345345345344456567889889895165654423236");
        Assert.assertEquals(d, ser.deserialize(ser.serialize(d)));
    }

    @Test
    public void testLocale() throws Exception {
        Serialization ser = new Serialization(null);
        Assert.assertEquals(Locale.FRANCE, ser.deserialize(ser.serialize(Locale.FRANCE)));
        Assert.assertEquals(Locale.CANADA_FRENCH, ser.deserialize(ser.serialize(Locale.CANADA_FRENCH)));
        Assert.assertEquals(Locale.SIMPLIFIED_CHINESE, ser.deserialize(ser.serialize(Locale.SIMPLIFIED_CHINESE)));

    }
}
