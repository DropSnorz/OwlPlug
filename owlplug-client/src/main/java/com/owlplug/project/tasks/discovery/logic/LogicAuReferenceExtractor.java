package com.owlplug.project.tasks.discovery.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class LogicAuReferenceExtractor {

  private static final int FOUR_CC_LENGTH = 4;
  private static final int NAME_LOOKBACK_LENGTH = 200;
  private static final Set<String> AU_TYPE_CODES = Set.of("aumu", "aumf", "aufx");
  private static final Pattern CHANNEL_STRIP_NAME = Pattern.compile(
      "^(Audio|Inst|Bus|Aux|Output|Master)(\\s+\\d+(-\\d+)?)?$");

  public List<LogicAuReference> extract(byte[] projectData) {
    List<LogicAuReference> references = new ArrayList<>();
    int firstPossibleOffset = FOUR_CC_LENGTH;
    int lastPossibleOffset = projectData.length - (FOUR_CC_LENGTH * 2);

    for (int offset = firstPossibleOffset; offset <= lastPossibleOffset; offset++) {
      String typeCode = readReversedFourCc(projectData, offset);
      if (!AU_TYPE_CODES.contains(typeCode)
          || !isPrintableFourCc(projectData, offset - FOUR_CC_LENGTH)
          || !isPrintableFourCc(projectData, offset + FOUR_CC_LENGTH)) {
        continue;
      }

      references.add(new LogicAuReference(
          typeCode,
          readReversedFourCc(projectData, offset + FOUR_CC_LENGTH),
          readReversedFourCc(projectData, offset - FOUR_CC_LENGTH),
          extractDisplayName(projectData, offset),
          offset));
    }

    return List.copyOf(references);
  }

  public List<LogicAuReference> extractFromActiveUserTracks(byte[] projectData) {
    List<TrackStrip> trackStrips = findTrackStrips(projectData);
    List<LogicAuReference> activeReferences = new ArrayList<>();
    int trackIndex = 0;
    TrackStrip owningTrack = null;

    for (LogicAuReference reference : extract(projectData)) {
      while (trackIndex < trackStrips.size()
          && trackStrips.get(trackIndex).offset() <= reference.offset()) {
        owningTrack = trackStrips.get(trackIndex++);
      }
      if (owningTrack != null && owningTrack.active() && owningTrack.isUserTrack()) {
        activeReferences.add(reference);
      }
    }

    return List.copyOf(activeReferences);
  }

  private List<TrackStrip> findTrackStrips(byte[] data) {
    List<TrackStrip> trackStrips = new ArrayList<>();
    int lastPossibleMarker = data.length - 24;

    for (int markerOffset = 1; markerOffset <= lastPossibleMarker; markerOffset++) {
      if (data[markerOffset - 1] != 0 || data[markerOffset] != 0x20) {
        continue;
      }

      int fieldEnd = markerOffset + 16;
      int nameEnd = markerOffset + 1;
      if (!isTrackNameCharacter(data[nameEnd], true)) {
        continue;
      }
      while (nameEnd < fieldEnd && isTrackNameCharacter(data[nameEnd], false)) {
        nameEnd++;
      }
      if (!isZeroPadded(data, nameEnd, fieldEnd)) {
        continue;
      }

      int descriptorOffset = fieldEnd;
      if ((data[descriptorOffset + 3] & 0xc0) != 0xc0) {
        continue;
      }

      int head = Byte.toUnsignedInt(data[descriptorOffset]);
      int second = Byte.toUnsignedInt(data[descriptorOffset + 1]);
      int third = Byte.toUnsignedInt(data[descriptorOffset + 2]);
      boolean isAudioOrInstrument = (head == 0xab && second != 0xf5)
          || (head == 0x29 && (third == 0xf3 || third == 0xf7));
      boolean isOtherChannelStrip = head == 0xe9 || head == 0x89 || head == 0x49
          || (head == 0xab && second == 0xf5)
          || (head == 0x29 && third != 0xf3 && third != 0xf7);
      if (!isAudioOrInstrument && !isOtherChannelStrip) {
        continue;
      }

      boolean active = (third & 0x04) != 0 || data[descriptorOffset + 4] != 0;
      trackStrips.add(new TrackStrip(markerOffset, active, isAudioOrInstrument));
    }

    return trackStrips;
  }

  private boolean isTrackNameCharacter(byte value, boolean firstCharacter) {
    int character = Byte.toUnsignedInt(value);
    return character >= (firstCharacter ? 0x21 : 0x20) && character <= 0x7e;
  }

  private boolean isZeroPadded(byte[] data, int start, int end) {
    for (int index = start; index < end; index++) {
      if (data[index] != 0) {
        return false;
      }
    }
    return true;
  }

  private String extractDisplayName(byte[] data, int markerOffset) {
    int start = Math.max(0, markerOffset - NAME_LOOKBACK_LENGTH);
    String lastCandidate = null;
    int runStart = -1;

    for (int index = start; index <= markerOffset; index++) {
      boolean printable = index < markerOffset && Byte.toUnsignedInt(data[index]) >= 0x20
          && Byte.toUnsignedInt(data[index]) <= 0x7e;
      if (printable && runStart < 0) {
        runStart = index;
      } else if (!printable && runStart >= 0) {
        String candidate = cleanDisplayName(data, runStart, index);
        if (candidate != null) {
          lastCandidate = candidate;
        }
        runStart = -1;
      }
    }

    return lastCandidate == null ? "Unknown Audio Unit" : lastCandidate;
  }

  private String cleanDisplayName(byte[] data, int start, int end) {
    if (end - start <= FOUR_CC_LENGTH) {
      return null;
    }

    String candidate = new String(data, start, end - start, java.nio.charset.StandardCharsets.US_ASCII)
        .replaceAll("<[^>]+>", "")
        .trim();
    if (candidate.length() <= FOUR_CC_LENGTH
        || candidate.contains("$class")
        || candidate.contains("NS.")
        || candidate.contains("bplist")
        || candidate.contains("WNS.")
        || CHANNEL_STRIP_NAME.matcher(candidate).matches()) {
      return null;
    }
    return candidate;
  }

  private boolean isPrintableFourCc(byte[] data, int offset) {
    for (int index = offset; index < offset + FOUR_CC_LENGTH; index++) {
      int value = Byte.toUnsignedInt(data[index]);
      if (value < 0x20 || value > 0x7e) {
        return false;
      }
    }
    return true;
  }

  private String readReversedFourCc(byte[] data, int offset) {
    StringBuilder value = new StringBuilder(FOUR_CC_LENGTH);
    for (int index = offset + FOUR_CC_LENGTH - 1; index >= offset; index--) {
      value.append((char) Byte.toUnsignedInt(data[index]));
    }
    return value.toString();
  }

  private record TrackStrip(int offset, boolean active, boolean isUserTrack) {
  }
}