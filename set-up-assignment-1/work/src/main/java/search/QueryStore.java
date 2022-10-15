package search;

class QueryStore {
    private int id;
    private String content;

    public QueryStore(int id, String content) {
        this.id = id;
        this.content = content;
    }

    public int getId() {
        return this.id;
    }

    public String getContent() {
        return this.content;
    }
}