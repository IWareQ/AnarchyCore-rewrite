package me.iwareq.anarchy.util;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class CompletableFutureArray {

	private final ArrayList<CompletableFuture<Void>> futures = new ArrayList<>();

	public void add(Runnable runnable) {
		futures.add(CompletableFuture.runAsync(runnable));
	}

	public void execute() {
		CompletableFuture<Void>[] array = (CompletableFuture<Void>[]) futures.toArray(new CompletableFuture[0]);
		CompletableFuture.allOf(array).join();
	}
}
