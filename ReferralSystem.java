// package RefSys;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class Value implements Serializable {

  private static final long serialVersionUID = 1L;
  private String TEXTUAL_INFO;
  private List<String> CONTIGUOUS_INFO;

  public Value(String TEXTUAL_INFO, List<String> CONTIGUOUS_INFO) {
    this.TEXTUAL_INFO = TEXTUAL_INFO;
    this.CONTIGUOUS_INFO = CONTIGUOUS_INFO;
  }

  public List<String> getContiguousInfo() {
    return CONTIGUOUS_INFO;
  }

  public void addContiguousInfo(String info) {
    this.CONTIGUOUS_INFO.add(info);
  }

  public void removeContiguousInfo(String info) {
    if (!this.CONTIGUOUS_INFO.isEmpty()) this.CONTIGUOUS_INFO.remove(info);
  }

  public void editStatus(String info) {
    this.TEXTUAL_INFO = info;
  }

  @Override
  public String toString() {
    return (TEXTUAL_INFO == null) ? CONTIGUOUS_INFO.toString() : TEXTUAL_INFO;
  }
}

class Monitor implements Serializable {

  private static final long serialVersionUID = 1L;

  protected Value ResumePath;
  protected Value status;
  protected Value ReferralRequestsSentTo;

  public Monitor(Value ResumePath, Value status, Value ReferralRequestsSentTo) {
    this.ResumePath = ResumePath;
    this.status = status;
    this.ReferralRequestsSentTo = ReferralRequestsSentTo;
  }

  public Value getStatus() {
    return status;
  }

  public void setStatus(Value v) {
    this.status = v;
  }

  public Value getReferralRequestsSentTo() {
    return ReferralRequestsSentTo;
  }

  public Value getStatusObject() {
    return status;
  }

  public void setReferralRequestsSentTo(Value v) {
    this.ReferralRequestsSentTo = v;
  }

  @Override
  public String toString() {
    return (
      "\n{" +
      "\n\tresume: " +
      this.ResumePath +
      ",\n\tstatus: " +
      this.status +
      ",\n\treferral_req_sent_to: " +
      this.ReferralRequestsSentTo +
      "\n}"
    );
  }
}

interface Company {
  public void commit(String k, Monitor v, Commit com);
}

class Microsoft implements Serializable, Company {

  protected Map<String, Monitor> get_serialized_buffer() throws Exception {
    File file = new File("./microsoft.ser");
    if (!file.exists()) return new HashMap<>();
    try (
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))
    ) {
      @SuppressWarnings("unchecked")
      Map<String, Monitor> data = (Map<String, Monitor>) in.readObject();
      return data;
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      return new HashMap<>();
    }
  }

  private static final long serialVersionUID = 1L;

  protected Map<String, Monitor> object;

  public Microsoft() {
    try {
      this.object = get_serialized_buffer();
    } catch (Exception e) {
      e.printStackTrace();
      this.object = new HashMap<>();
    }
  }

  @Override
  public void commit(String k, Monitor v, Commit commit_object) {
    this.object.put(k, v);
    try {
      serialize_buffer();
      commit_object.onCommitSuccessful();
    } catch (Exception e) {
      commit_object.onCommitUnsuccessful();
    }
  }

  private void serialize_buffer() throws Exception {
    FileOutputStream fops = new FileOutputStream("./microsoft.ser");
    ObjectOutputStream objops = new ObjectOutputStream(fops);
    objops.writeObject(object);
    objops.close();
    fops.close();
  }
}

interface Commit {
  public void onCommitSuccessful();

  public void onCommitUnsuccessful();
}

public class ReferralSystem {

  public static void main(String[] args) {
    if (args[0].equals("microsoft")) {
      String command = args[1];
      Company ms = new Microsoft();
      Microsoft msCast = (Microsoft) ms;
      Monitor buffer = null;
      if (args.length > 2) buffer = msCast.object.get(args[2]);
      switch (command) {
        case "commit":
          // java ReferralSystem microsot commit JOB_ID RESUME_PATH
          Monitor mntr = null;
          if (buffer == null) mntr = new Monitor(
            new Value(args[3], null),
            null,
            null
          );
          if (buffer != null) buffer.ResumePath = new Value(args[3], null);
          ms.commit(
            args[2],
            (buffer == null ? mntr : buffer),
            new Commit() {
              @Override
              public void onCommitSuccessful() {
                System.out.println("Commit successful");
              }

              @Override
              public void onCommitUnsuccessful() {
                System.out.println("Try again");
              }
            }
          );
          break;
        case "view":
          // java ReferralSystem microsot view JOB_ID
          System.out.println(((Microsoft) ms).object.get(args[2]));
          break;
        case "admit-ref":
          // java ReferralSystem microsot admit-ref JOB_ID NAME
          if (buffer != null) {
            Value referrals = buffer.getReferralRequestsSentTo();
            if (referrals == null) {
              referrals = new Value(null, new ArrayList<>());
            }
            referrals.addContiguousInfo(args[3]);
            buffer.setReferralRequestsSentTo(referrals);
            ms.commit(
              args[2],
              buffer,
              new Commit() {
                @Override
                public void onCommitSuccessful() {
                  System.out.println("Referrer admitted successful");
                }

                @Override
                public void onCommitUnsuccessful() {
                  System.out.println("Try again");
                }
              }
            );
          }
          break;
        case "evict-ref":
          // java ReferralSystem microsot evict-ref JOB_ID NAME
          if (buffer != null) {
            Value referrals = buffer.getReferralRequestsSentTo();
            if (referrals == null) {
              referrals = new Value(null, new ArrayList<>());
            }
            referrals.removeContiguousInfo(args[3]);
            buffer.setReferralRequestsSentTo(referrals);
            ms.commit(
              args[2],
              buffer,
              new Commit() {
                @Override
                public void onCommitSuccessful() {
                  System.out.println("Referrer eviction successful");
                }

                @Override
                public void onCommitUnsuccessful() {
                  System.out.println("Try again");
                }
              }
            );
          }
          break;
        case "edit-status":
          // java ReferralSystem microsot edit-status JOB_ID referred
          if (buffer != null) {
            Value referrals = buffer.getStatusObject();
            if (referrals == null) {
              referrals = new Value(new String(), null);
            }
            referrals.editStatus(args[3]);
            buffer.setStatus(referrals);
            ms.commit(
              args[2],
              buffer,
              new Commit() {
                @Override
                public void onCommitSuccessful() {
                  System.out.println("Job status edited successful");
                }

                @Override
                public void onCommitUnsuccessful() {
                  System.out.println("Try again");
                }
              }
            );
          }
          break;
        case "project":
          System.out.println(msCast.object);

          break;
        default:
          break;
      }
    } else if (args[0].equals("amazon")) {}
  }
}
