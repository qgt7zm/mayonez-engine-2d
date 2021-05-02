package com.mayonez;

import com.util.Vector2;

public class Transform {

	public Vector2 position, scale;

	public Transform(Vector2 position, Vector2 scale) {
		this.position = position;
		this.scale = scale;
	}
	
	public void move(Vector2 displacement) {
		position = position.add(displacement);
	}
	
	@Override
	public String toString() {
		return String.format("Position: %s, Scale: %s", position, scale);
	}

}
