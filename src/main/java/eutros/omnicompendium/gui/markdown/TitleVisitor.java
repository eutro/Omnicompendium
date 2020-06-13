package eutros.omnicompendium.gui.markdown;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;

public class TitleVisitor extends AbstractVisitor {

    public String title = null;
    private boolean inTitle = false;

    @Override
    public void visit(Text text) {
        if(inTitle) {
            title = text.getLiteral();
        }
    }

    @Override
    public void visit(Heading heading) {
        inTitle = true;
        visitChildren(heading);
        inTitle = false;
    }

    @Override
    protected void visitChildren(Node parent) {
        Node node = parent.getFirstChild();
        while(node != null && title == null) {
            Node next = node.getNext();
            node.accept(this);
            node = next;
        }
    }

}
