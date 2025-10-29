package uk.co.mruoc.cws.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CrosswordSolverApp {

  public static void main(String[] args) {
    SpringApplication.run(CrosswordSolverApp.class, args);
  }

  // POST a completion to an attempt to the hackathon API
  // POST to auto create an attempt at a puzzle and solve by posting answers automatically
  // POST to trigger loading all puzzles from hackathon API
}
