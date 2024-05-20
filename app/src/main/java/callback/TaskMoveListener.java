package callback;

public interface TaskMoveListener {
    boolean onTaskMove(int fromPosition, int toPosition);
    void onTaskSwiped(int position);
}
