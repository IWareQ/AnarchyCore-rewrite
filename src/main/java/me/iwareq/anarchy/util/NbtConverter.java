package me.iwareq.anarchy.util;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;

import java.io.IOException;

public class NbtConverter {

	private static final char[] HEX_CODE = "0123456789ABCDEF".toCharArray();

	public static String toHex(CompoundTag namedTag) {
		if (namedTag == null) {
			return "";
		}

		try {
			byte[] data = NBTIO.write(namedTag);

			StringBuilder builder = new StringBuilder(data.length * 2);
			for (byte b : data) {
				builder.append(HEX_CODE[(b >> 4) & 0xF]);
				builder.append(HEX_CODE[(b & 0xF)]);
			}

			return builder.toString();
		} catch (IOException exception) {
			exception.printStackTrace();
		}

		return "";
	}

	public static CompoundTag toNbt(String hex) {
		if (hex.isEmpty()) {
			return new CompoundTag();
		}

		int len = hex.length();

		// "111" is not a valid hex encoding.
		if (len % 2 != 0) {
			throw new IllegalArgumentException("Hex needs to be even-length: " + hex);
		}

		byte[] out = new byte[len / 2];

		for (int i = 0; i < len; i += 2) {
			int h = hexToBin(hex.charAt(i));
			int l = hexToBin(hex.charAt(i + 1));
			if (h == -1 || l == -1) {
				throw new IllegalArgumentException("contains illegal character for hex: " + hex);
			}

			out[i / 2] = (byte) (h * 16 + l);
		}

		try {
			return NBTIO.read(out);
		} catch (IOException exception) {
			exception.printStackTrace();
		}

		return new CompoundTag();
	}

	private static int hexToBin(char ch) {
		if ('0' <= ch && ch <= '9') {
			return ch - '0';
		}

		if ('A' <= ch && ch <= 'F') {
			return ch - 'A' + 10;
		}

		if ('a' <= ch && ch <= 'f') {
			return ch - 'a' + 10;
		}

		return -1;
	}
}
