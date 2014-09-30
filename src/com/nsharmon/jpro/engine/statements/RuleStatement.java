package com.nsharmon.jpro.engine.statements;

import com.nsharmon.jpro.engine.PrologProgram;
import com.nsharmon.jpro.tokenizer.PrologTokenType;

public class RuleStatement implements Statement<PrologProgram> {
	private final FactStatement left, right;
	
	public RuleStatement(final FactStatement left, final FactStatement right) {
		this.left = left;
		this.right = right;
	}
	
	public void run(final PrologProgram program) {
		program.getFactsMapping().addConclusion(this);
	}

	public FactStatement getLeft() {
		return left;
	}

	public FactStatement getRight() {
		return right;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(left);
		sb.append(" ");
		sb.append(PrologTokenType.HORNOPER.getCode());
		sb.append(" ");
		sb.append(right);
		sb.append(PrologTokenType.CLOSE.getCode());
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final RuleStatement other = (RuleStatement) obj;
		if (left == null) {
			if (other.left != null) {
				return false;
			}
		} else if (!left.equals(other.left)) {
			return false;
		}
		if (right == null) {
			if (other.right != null) {
				return false;
			}
		} else if (!right.equals(other.right)) {
			return false;
		}
		return true;
	}
	
	
}
