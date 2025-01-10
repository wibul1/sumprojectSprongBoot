package com.example.project.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.SearchTransactionDTO;
import com.example.project.model.ExtractedData;
import com.example.project.model.TransactionModel;
import com.example.project.repository.TransactionRepository;
import com.example.project.service.OCRService;
import com.example.project.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private OCRService ocrService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;


    @PostMapping("/upload")
    public ResponseEntity<?> uploadTransactions(@RequestParam("file") MultipartFile[] files) throws IOException {
        if (files.length == 0) {
            return ResponseEntity.badRequest().body("No files uploaded");
        }

        List<ExtractedData> results = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
                try {
                    file.transferTo(tempFile);
                    
                    // ใช้ OCR เพื่อดึงข้อความจากภาพ
                    String extractedText = ocrService.extractTextFromImage(tempFile);
                    
                    try {
                        // แปลง JSON string เป็น Object
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode jsonNode = mapper.readTree(extractedText);

                        // ดึงข้อมูลจาก JSON
                        String amount = jsonNode.get("amount").asText();
                        String date = jsonNode.get("date").asText();
                        String time = jsonNode.get("time").asText();

                        // แยกเดือนและปีจากวันที่
                        String month = "";
                        String year = "";
                        if (date != null && !date.isEmpty()) {
                            String[] dateParts = date.split("/");
                            if (dateParts.length == 3) {
                                month = dateParts[1]; // เดือนอยู่ในตำแหน่งที่ 2
                                year = dateParts[2];  // ปีอยู่ในตำแหน่งที่ 3
                            }
                        }

                        // สร้างและบันทึก TransactionModel
                        TransactionModel transaction = new TransactionModel();
                        transaction.setRawText(extractedText);
                        transaction.setAmount(amount);
                        transaction.setDate(date);
                        transaction.setTime(time);
                        transaction.setFileName(file.getOriginalFilename());
                        transaction.setMonth(month); // เพิ่มเดือน
                        transaction.setYear(year);   // เพิ่มปี

                        TransactionModel savedTransaction = transactionRepository.save(transaction);
                        results.add(new ExtractedData(extractedText, savedTransaction));

                    } catch (JsonProcessingException e) {
                        errors.add("Error processing " + file.getOriginalFilename() + ": " + e.getMessage());
                    }
                } catch (Exception e) {
                    errors.add("Error processing " + file.getOriginalFilename() + ": " + e.getMessage());
                } finally {
                    tempFile.delete();
                }
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        if (!errors.isEmpty()) {
            response.put("errors", errors);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchTransactions(@Valid @RequestBody SearchTransactionDTO request) {
        try {
            List<TransactionModel> transactions = 
                transactionService.getTransactions(request.getMonth(), request.getYear());
            
            Map<String, Object> data = new HashMap<>();
            data.put("transactions", transactions);
            data.put("total", transactions.size());
            
            if (transactions.isEmpty()) {
                return ResponseEntity.ok()
                    .body(new ApiResponse<>(
                        "No transactions found for the specified criteria",
                        data
                    ));
            }
            
            return ResponseEntity.ok()
                .body(new ApiResponse<>(
                    "Transactions retrieved successfully",
                    data
                ));
                
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(
                    "Error retrieving transactions: " + e.getMessage(),
                    null
                ));
        }
    }



    // @PostMapping("/upload")
    // public ResponseEntity<?> uploadTransaction(@RequestParam("file") MultipartFile file) throws IOException {
    //     if (file.isEmpty()) {
    //         return ResponseEntity.badRequest().body("File is empty");
    //     }

    //     File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
    //     file.transferTo(tempFile);

    //     try {
    //         // เรียกใช้ Python OCR Service
    //         String extractedText = ocrService.extractTextFromImage(tempFile);

    //         // ดึงข้อมูลสำคัญจากข้อความ
    //         String amount = extractData(extractedText, "\\d+\\.\\d{2}\\s?บาท");
    //         String date = extractData(extractedText, "\\d{1,2}\\s+ธ\\.ค\\.\\s+\\d{4}");
    //         String time = extractData(extractedText, "\\d{1,2}:\\d{2}");

    //         // สร้าง Transaction Model
    //         TransactionModel transaction = new TransactionModel();
    //         transaction.setRawText(extractedText);
    //         transaction.setAmount(amount);
    //         transaction.setDate(date);
    //         transaction.setTime(time);

    //         // บันทึกลง MongoDB
    //         TransactionModel savedTransaction = transactionRepository.save(transaction);

    //         return ResponseEntity.ok(new ExtractedData(extractedText, savedTransaction));
    //     } finally {
    //         tempFile.delete();
    //     }
    // }





    // @PostMapping("/upload")
    // public ResponseEntity<?> uploadTransaction(@RequestParam("file") MultipartFile file) throws IOException {
    //     if (file.isEmpty()) {
    //         return ResponseEntity.badRequest().body("File is empty");
    //     }

    //     File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
    //     file.transferTo(tempFile);

    //     try {
    //         String extractedText = ocrService.extractTextFromImage(tempFile);
    //         System.err.println("tempFile"+ tempFile );
    //         String amount = extractData(extractedText, "\\d+\\.\\d{2}\\s?บาท");
    //         String date = extractData(extractedText, "\\d{1,2}\\s+ธ\\.ค\\.\\s+\\d{4}");
    //         String time = extractData(extractedText, "\\d{1,2}:\\d{2}");

    //         TransactionModel transaction = new TransactionModel();
    //         transaction.setRawText(extractedText);
    //         transaction.setAmount(amount);
    //         transaction.setDate(date);
    //         transaction.setTime(time);

    //         TransactionModel savedTransaction = transactionRepository.save(transaction);

    //         return ResponseEntity.ok(new ExtractedData(extractedText, savedTransaction));
    //     } finally {
    //         tempFile.delete();
    //     }
    // }

//     @PostMapping("/upload")
// public ResponseEntity<?> uploadTransaction(@RequestParam("file") MultipartFile file) throws IOException {
//     if (file.isEmpty()) {
//         return ResponseEntity.badRequest().body("File is empty");
//     }

//     File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
//     file.transferTo(tempFile);

//     try {
//         // ใช้ OCR เพื่อดึงข้อความจากภาพ
//         String extractedText = ocrService.extractTextFromImage(tempFile);
//         System.out.println("Extracted Text: " + extractedText); // พิมพ์ข้อความที่ถูกดึงออกมา

//         String amount = extractData(extractedText, "\\d+\\.\\d{2}\\s?[บ-ฮ]+");
//         System.out.println("Amount: " + amount); // พิมพ์จำนวนเงิน

//         String date = extractData(extractedText, "\\d{1,2}\\s*[ก-ฮ]\\.?\\s*\\d{4}");
//         System.out.println("Date: " + date); // พิมพ์วันที่

//         String time = extractData(extractedText, "\\d{1,2}:\\d{2}");
//         System.out.println("Time: " + time); // พิมพ์เวลา

//         // สร้างวัตถุ TransactionModel และบันทึกข้อมูลใน MongoDB
//         TransactionModel transaction = new TransactionModel();
//         transaction.setRawText(extractedText);
//         transaction.setAmount(amount);
//         transaction.setDate(date);
//         transaction.setTime(time);

//         TransactionModel savedTransaction = transactionRepository.save(transaction);

//         return ResponseEntity.ok(new ExtractedData(extractedText, savedTransaction));
//     } finally {
//         tempFile.delete();
//     }
// }

// @PostMapping("/upload")
// public ResponseEntity<?> uploadTransaction(@RequestParam("file") MultipartFile file) throws IOException {
//     if (file.isEmpty()) {
//         return ResponseEntity.badRequest().body("File is empty");
//     }

//     File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
//     file.transferTo(tempFile);

//     try {
//         // ใช้ OCR เพื่อดึงข้อความจากภาพ
//         String extractedText = ocrService.extractTextFromImage(tempFile);
//         System.out.println("Extracted Text: " + extractedText);

//         // แปลง JSON string เป็น Object
//         ObjectMapper mapper = new ObjectMapper();
//         JsonNode jsonNode = mapper.readTree(extractedText);

//         // ดึงข้อมูลจาก JSON
//         String amount = jsonNode.get("amount").asText();
//         String date = jsonNode.get("date").asText();
//         String time = jsonNode.get("time").asText();

//         System.out.println("Amount: " + amount);
//         System.out.println("Date: " + date);
//         System.out.println("Time: " + time);

//         // สร้างวัตถุ TransactionModel และบันทึกข้อมูลใน MongoDB
//         TransactionModel transaction = new TransactionModel();
//         transaction.setRawText(extractedText);
//         transaction.setAmount(amount);
//         transaction.setDate(date);
//         transaction.setTime(time);

//         TransactionModel savedTransaction = transactionRepository.save(transaction);

//         return ResponseEntity.ok(new ExtractedData(extractedText, savedTransaction));
//     } catch (JsonProcessingException e) {
//         return ResponseEntity.badRequest().body("Invalid JSON format: " + e.getMessage());
//     } finally {
//         tempFile.delete();
//     }
// }



//     private String extractData(String text, String regex) {
//         Pattern pattern = Pattern.compile(regex);
//         Matcher matcher = pattern.matcher(text);
//         return matcher.find() ? matcher.group() : null;
//     }


// Java Controller
// @PostMapping("/upload")
// public ResponseEntity<?> uploadTransactions(@RequestParam("file") MultipartFile[] files) throws IOException {
//     if (files.length == 0) {
//         return ResponseEntity.badRequest().body("No files uploaded");
//     }

//     List<ExtractedData> results = new ArrayList<>();
//     List<String> errors = new ArrayList<>();

//     for (MultipartFile file : files) {
//         if (!file.isEmpty()) {
//             File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
//             try {
//                 file.transferTo(tempFile);
                
//                 // ใช้ OCR เพื่อดึงข้อความจากภาพ
//                 String extractedText = ocrService.extractTextFromImage(tempFile);
                
//                 try {
//                     // แปลง JSON string เป็น Object
//                     ObjectMapper mapper = new ObjectMapper();
//                     JsonNode jsonNode = mapper.readTree(extractedText);

//                     // ดึงข้อมูลจาก JSON
//                     String amount = jsonNode.get("amount").asText();
//                     String date = jsonNode.get("date").asText();
//                     String time = jsonNode.get("time").asText();

//                     // สร้างและบันทึก TransactionModel
//                     TransactionModel transaction = new TransactionModel();
//                     transaction.setRawText(extractedText);
//                     transaction.setAmount(amount);
//                     transaction.setDate(date);
//                     transaction.setTime(time);
//                     transaction.setFileName(file.getOriginalFilename());  // เพิ่มชื่อไฟล์

//                     TransactionModel savedTransaction = transactionRepository.save(transaction);
//                     results.add(new ExtractedData(extractedText, savedTransaction));

//                 } catch (JsonProcessingException e) {
//                     errors.add("Error processing " + file.getOriginalFilename() + ": " + e.getMessage());
//                 }
//             } catch (Exception e) {
//                 errors.add("Error processing " + file.getOriginalFilename() + ": " + e.getMessage());
//             } finally {
//                 tempFile.delete();
//             }
//         }
//     }

//     Map<String, Object> response = new HashMap<>();
//     response.put("results", results);
//     if (!errors.isEmpty()) {
//         response.put("errors", errors);
//     }

//     return ResponseEntity.ok(response);
// }


    
}
