// interfaces/FilterData.ts

export interface FilterData {
  senderBank?: string;
  receiverBank?: string;
  date?: [string, string];
  status?: string;
  inn?: string;
  amountRange?: [number, number];
  transactionType?: string;
  category?: string;
}
