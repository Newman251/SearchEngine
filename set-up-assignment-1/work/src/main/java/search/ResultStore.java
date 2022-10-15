package search;

class ResultStore {

    private int queryId, docId, rank;
    private float score;

    public ResultStore(int query, int doc, int rank, float score) {
        this.queryId = query;
        this.docId = doc;
        this.rank = rank;
        this.score = score;
    }

    public String toTrecEvalFormat() {
        return Integer.toString(queryId) + "\t"
            + "Q0\t"
            + Integer.toString(docId) + "\t"
            + Integer.toString(rank) + "\t"
            + Float.toString(score) + "\t"
            + "Exp";
    }
}