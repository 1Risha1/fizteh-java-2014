package ru.fizteh.java2.podorozhnaya.filemap.storeabledbimpl;

import ru.fizteh.java2.podorozhnaya.filemap.utils.FileStorage;
import ru.fizteh.java2.podorozhnaya.filemap.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class GenericTable<ValueType> {
    protected final String name;
    protected Lock lock = new ReentrantLock(true);
    protected final File tableDirectory;
    private final LazyMultiFileHashMap<ValueType> oldDatabase;
    private final ThreadLocal<Map<String, ValueType>> changedValues = new ThreadLocal<Map<String, ValueType>>() {
        @Override
        protected Map<String, ValueType> initialValue() {
            return new HashMap<>();
        }
    };

    public GenericTable(String name, File rootDir) {
        tableDirectory = new File(rootDir, name);
        if (!tableDirectory.isDirectory()) {
            throw new IllegalArgumentException(name + "not exist");
        }
        try {
            oldDatabase = new LazyMultiFileHashMap<>(tableDirectory, this);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        this.name = name;
    }

    public ValueType get(String key) {
        checkKey(key);
        if (changedValues.get().containsKey(key)) {
            return changedValues.get().get(key);
        } else {
            try {
                lock.lock();
                return  oldDatabase.get(key);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            } finally {
                lock.unlock();
            }
        }
    }

    public ValueType remove(String key) {
        checkKey(key);
        ValueType res = get(key);
        changedValues.get().put(key, null);
        return res;
    }

    public ValueType put(String key, ValueType value) {
        checkKey(key);
        if (value == null) {
            throw new IllegalArgumentException("null value");
        }
        ValueType res = get(key);
        changedValues.get().put(key, value);
        return res;
    }

    private int countChanges() {
        lock.lock();
        int res = 0;
        for (String s: changedValues.get().keySet()) {
            try {
                if (!checkEquals(changedValues.get().get(s), oldDatabase.get(s))) {
                    ++res;
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
        lock.unlock();
        return res;
    }

    protected boolean checkEquals(ValueType val1, ValueType val2) {
        if (val1 == null && val2 == null) {
            return true;
        }
        return val1 != null && val2 != null && val1.equals(val2);
    }

    public int commit() throws IOException {
        ThreadLocal<Map<Integer, Map<String, ValueType>>> database =
                new ThreadLocal<Map<Integer, Map<String, ValueType>>>() {
            @Override
            protected Map<Integer, Map<String, ValueType>> initialValue() {
                return new HashMap<>();
            }
        };
        ThreadLocal<Set<Integer>> filesToUpdate = new ThreadLocal<Set<Integer>>() {
            @Override
            protected Set<Integer> initialValue() {
                return new HashSet<>();
            }
        };

        int res = countChanges();
        oldDatabase.commitSize(size());
        try {
            lock.lock();
            for (Map.Entry<String, ValueType> s: changedValues.get().entrySet()) {
                int nfile = Utils.getNumberOfFile(s.getKey());
                filesToUpdate.get().add(nfile);
                if (database.get().get(nfile) == null) {
                    database.get().put(nfile, new HashMap<String, ValueType>());
                }
                database.get().get(nfile).put(s.getKey(), s.getValue());
            }

            for (Integer nfile: filesToUpdate.get()) {
                Map<String, ValueType> data = oldDatabase.putAllInMap(nfile, database.get().get(nfile));
                FileStorage.commitDiff(getFile(nfile), serialize(data));
            }

            for (int i = 0; i < 16; ++i) {
                File dir = new File(tableDirectory, i + ".dir");
                dir.delete();
            }
        } finally {
            lock.unlock();
        }
        changedValues.get().clear();
        return res;
    }


    private File getFile(int nfile) throws IOException {
        File dir = new File(tableDirectory, nfile / 16 + ".dir");
        if (!dir.isDirectory()) {
            if (!dir.mkdir()) {
                throw new IOException("can't create directory");
            }
        }
        File db = new File(dir, nfile % 16 + ".dat");
        if (!db.exists()) {
            if (!db.createNewFile()) {
                throw new IOException("can't create file");
            }
        }
        return db;
    }

    protected abstract Map<String, String> serialize(Map<String, ValueType> values);
    protected abstract Map<String, ValueType> deserialize(Map<String, String> values) throws IOException;

    public int rollback() {
        int res = countChanges();
        changedValues.get().clear();
        return res;
    }

    public int getChangedValuesNumber() {
        return changedValues.get().size();
    }

    public String getName() {
        return name;
    }

    public int size() {
        lock.lock();
        int res = oldDatabase.size();
        for (Map.Entry<String, ValueType> s: changedValues.get().entrySet()) {
            try {
                if (s.getValue() == null && oldDatabase.get(s.getKey()) != null) {
                    --res;
                } else if (s.getValue() != null && oldDatabase.get(s.getKey()) == null) {
                    ++res;
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
        lock.unlock();
        return res;
    }

    private void checkKey(String key) throws IllegalArgumentException {
        if (key == null || key.matches("(.*\\s+.*)*")) {
            throw new IllegalArgumentException("key or value null or empty or contain spaces");
        }
    }
}
