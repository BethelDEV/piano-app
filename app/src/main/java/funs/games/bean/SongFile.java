package funs.games.bean;

/**
 * @ProjectName: pianoli_HG
 * @Package: funs.games.bean
 * @ClassName: SongFile
 * @Description: 乐谱(文件)描述
 */
public class SongFile {
    private String id;
    private String name;
    private String file;

    public SongFile(String name, String file) {
        this(file, name, file);
    }

    public SongFile(String id, String name, String file) {
        this.id = id;
        this.name = name;
        this.file = file;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFile() {
        return file;
    }
}
